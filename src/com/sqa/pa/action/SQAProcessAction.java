/*
 * Copyright (c) 2007-2008 Caritor India Pvt Ltd.
 * All Rights Reserved
 * This work contains trade secrets and confidential material of Caritor India
 * Pvt Ltd., and its use of disclosure in whole or in part without express
 * written permission of Caritor India Pvt Ltd. is Prohibited.
 * File Name           : SQAProcessAction.java
 * Author              : Caritor India Pvt Ltd.
 * Date of Creation    :
 * Description         : Java file for processing actions.
 * Version No.	       : 1.1
 * Modification History:
 * Date         Version No.     Who     	Description
 * 10-Jul-07	1.1				Renuka 		Modified code in createProj and validateUser methods.
 *
 *
 */

package com.sqa.pa.action;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

//import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sqa.pa.bean.ActProjInfo;
import com.sqa.pa.bean.ActiveProjDetBean;
import com.sqa.pa.bean.CreateProjBean;
import com.sqa.pa.bean.DNKnownIssuesBean;
import com.sqa.pa.bean.DeliveryNotesBean;
import com.sqa.pa.bean.SQAReviewbean;
import com.sqa.pa.bean.WeeklyPlanBean;
import com.sqa.pa.bean.WeeklyStatusBean;
import com.sqa.pa.bean.WorkPlanBean;
import com.sqa.pa.dao.ActiveProjDetDao;
import com.sqa.pa.dao.CreateReviewDao;
import com.sqa.pa.dao.DNKnownIssuesDao;
import com.sqa.pa.dao.DeliveryNotesDao;
import com.sqa.pa.dao.NewProjDao;
import com.sqa.pa.dao.NewWeeklyPlanDao;
import com.sqa.pa.dao.NewWorkPlanDao;
import com.sqa.pa.dao.SQAProcessDao;
import com.sqa.pa.dao.WeeklyStatusDao;
import com.sqa.pa.mails.SqaMail;
import com.sqa.pa.reusable.SQAGeneric;

public class SQAProcessAction {

	public String validateUser(HttpServletRequest objRequest,
			HttpServletResponse objResponse) throws Exception {

		HttpSession objSession = objRequest.getSession(true);
		String strUserName = objRequest.getParameter("userId");
		String strPassword = objRequest.getParameter("password");
		strPassword = SQAGeneric.encryptDecrypt(strPassword, true);		//decrypts the password
		String strRole = objRequest.getParameter("role");


		SQAProcessDao objDAO = new SQAProcessDao();
		try
		{
			String strResult = objDAO.getUserRole(strUserName, strPassword, strRole);
			if (strResult != null )
			{
				objSession.setAttribute("userId", strUserName);
				objSession.setAttribute("role", strRole);
				return "valid";
			}
		}
		catch (Exception objExp)
		{
			objExp.printStackTrace();
		}
		return "invalid";
	}

	public String createProj(HttpServletRequest objRequest,
			HttpServletResponse objResponse) {
		CreateProjBean objProjBean = new CreateProjBean();

		objProjBean.setProjId(objRequest.getParameter("tbProjId"));
		objProjBean.setProjName(objRequest.getParameter("tbProjName"));
		objProjBean.setProjType(objRequest.getParameter("cbProjType"));
		objProjBean.setRegion(objRequest.getParameter("cbRegion"));
		objProjBean.setLocation(objRequest.getParameter("cbLocation"));

		objProjBean.setAccount(objRequest.getParameter("cbAccounts"));
		objProjBean.setVertical(objRequest.getParameter("cbVerticals"));

		objProjBean.setStrPMContact(objRequest.getParameter("tbSqaContactPM"));
		objProjBean.setStrQTLContact(objRequest.getParameter("tbSqaContactQTL"));

		String strWrkPlan = objRequest.getParameter("chkWorkPlan");
		String strResult = "";
		strWrkPlan = "on".equalsIgnoreCase(strWrkPlan) ? "Yes" : "No";
		objProjBean.setWorkPlan(strWrkPlan);
		try {
			NewProjDao objDAO = new NewProjDao();

			HttpSession session = objRequest.getSession(true);
			String strBtn = (String) session.getAttribute("sesBtn");

			if (strBtn != "Update") {
				strResult = objDAO.createProj(objProjBean);
			} else {
				strResult = objDAO.updateProject(objProjBean);
			}

		}

		catch (SQLException e) {

			HttpSession session = objRequest.getSession(false);
			session.setAttribute("UniqueProj", "Project ID already Exists");

		} catch (Exception e) {

		}
		return strResult;
	}

	public String activeProjDet(HttpServletRequest objRequest,
			HttpServletResponse objResponse) throws Exception {
		ActiveProjDetBean objActProjDetBean = new ActiveProjDetBean();
		ActProjInfo objActProjInfo = null;
		String strResult = "";
		String strBtnName = "";
		int intRCount = Integer.parseInt(objRequest.getParameter("hdnRCount"));
		objActProjDetBean.setStrProjId(objRequest.getParameter("tbProjId"));
		objActProjDetBean.setStrSqaTl(objRequest.getParameter("tbSqaTl"));
		objActProjDetBean.setStrSqaAudit(objRequest.getParameter("rbSqaAudit"));
		objActProjDetBean.setStrProjSDate(objRequest.getParameter("tbPlStDt"));
		objActProjDetBean.setStrProjEDate(objRequest.getParameter("tbPlEnDt"));
		objActProjDetBean.setStrKickOff(objRequest.getParameter("tbKickOff"));
		objActProjDetBean.setStrStatus(objRequest.getParameter("tbStatus"));
		objActProjDetBean.setStrAuditDate(objRequest
				.getParameter("tbSqaAuditDt"));
		objActProjDetBean.setStrDefPrev(objRequest.getParameter("rbDefPrev"));
		objActProjDetBean.setStrDocMgr(objRequest.getParameter("tbDocMgr"));
		objActProjDetBean.setStrAccMgr(objRequest.getParameter("tbProjAM"));
		objActProjDetBean.setStrComment(objRequest.getParameter("taComment"));
		objActProjDetBean.setStrVer(objRequest.getParameter("cbVertical"));
		objActProjDetBean.setStrAcct(objRequest.getParameter("cbAccount"));
		strBtnName = objRequest.getParameter("hdButton");
		ArrayList arlActInfo = new ArrayList();

		for (int i = 0; i < intRCount; i++)
		{
			objActProjInfo = new ActProjInfo();

			objActProjInfo.setStrProjEffort(objRequest.getParameter("tbProjEffort" + i));
			objActProjInfo.setStrSqaEffort(objRequest.getParameter("tbSqaEffort" + i));
			objActProjInfo.setStrEffortUOM(objRequest.getParameter("selUOM"+ i));
			objActProjInfo.setStrTechnology(objRequest.getParameter("tbTech"+ i));
			if (!("".equals(objRequest.getParameter("tbTech" + i))))
			{
				arlActInfo.add(objActProjInfo);
			}
		}
		objActProjDetBean.setArrActProjInfo(arlActInfo);

		try {
			ActiveProjDetDao activeProjDetDao = new ActiveProjDetDao();
			if ("Update".equalsIgnoreCase(strBtnName))
				strResult = activeProjDetDao
						.updateActProjDet(objActProjDetBean);
			else
				strResult = activeProjDetDao
						.createActProjDet(objActProjDetBean);

		} catch (Exception objExp) {

		}
		return strResult;
	}

	public String createWp(HttpServletRequest objRequest,
			HttpServletResponse objResponse) {
		int intRCount = Integer.parseInt(objRequest.getParameter("hdnRCount"));
		String strResult = "";
		WorkPlanBean objWrkPlan = new WorkPlanBean();
		NewWorkPlanDao objWpDao = new NewWorkPlanDao();

		//Create request on submittion of work plan - start

		String strProjectId  = objRequest.getParameter("cbProjId");
		String strSQATL = objRequest.getParameter("tbSqaTl");
		String strRqStatus="";
		String strRevReqStatus="";
		CreateReviewDao createReviewDao=new CreateReviewDao();
		String strUser = createReviewDao.getQtlforprojId(strProjectId);
		strUser = strUser.substring(0,strUser.indexOf("@"));
		strUser = strUser.replace('.',' ');

		//objWrkPlan.setProjId(objRequest.getParameter("cbProjId"));
		objWrkPlan.setProjId(strProjectId);

		//Create request on submittion of work plan - end

		objWrkPlan.setProjType(objRequest.getParameter("tbProjType"));
		objWrkPlan.setProjSDate(objRequest.getParameter("tbProjStDt"));
		objWrkPlan.setProjEDate(objRequest.getParameter("tbProjEnDt"));

		//Create request on submittion of work plan - start

		//objWrkPlan.setSqaTl(objRequest.getParameter("tbSqaTl"));
		objWrkPlan.setSqaTl(strSQATL);

		//Create request on submittion of work plan - end

		objWrkPlan.setBkSqaTl(objRequest.getParameter("tbBkSqaTl"));
		objWrkPlan.setStrName(objRequest.getParameter("tbProjName"));
		objWrkPlan.setRegion(objRequest.getParameter("tbRegion"));
		objWrkPlan.setProjMan(objRequest.getParameter("tbProjMgr"));
		objWrkPlan.setAccMan(objRequest.getParameter("tbAccMgr"));
		objWrkPlan.setCrtdBy(objRequest.getParameter("tbPrepBy"));
		objWrkPlan.setCrtdDate(objRequest.getParameter("tbPrepDt"));
		objWrkPlan.setStrApprBy(objRequest.getParameter("tbAppBy"));
		objWrkPlan.setStrApprDt(objRequest.getParameter("tbAppDt"));
		objWrkPlan.setRCount(intRCount);
		objWrkPlan.setActArray(new ArrayList());
		String strAct = "";
		String strAssTo = "";
		String strPlStDt = "";
		String strPlEnDt = "";
		String strAcStDt = "";
		String strActEnDt = "";
		String strStd = "";
		String strRem = "";

		try {
			for (int i = 0; i < intRCount; i++) {

				strAct = "tbActivity" + i;
				strAssTo = "tbActAssTo" + i;
				strPlStDt = "tbActPlnStDt" + i;
				strPlEnDt = "tbActPlnEnDt" + i;
				strAcStDt = "tbActStDt" + i;
				strActEnDt = "tbActEnDt" + i;
				strStd = "tbActStds" + i;
				strRem = "tbActRmks" + i;
				objWrkPlan.addActivity(objRequest.getParameter(strAct),
						objRequest.getParameter(strAssTo), objRequest
								.getParameter(strPlStDt), objRequest
								.getParameter(strPlEnDt), objRequest
								.getParameter(strAcStDt), objRequest
								.getParameter(strActEnDt), objRequest
								.getParameter(strStd), objRequest
								.getParameter(strRem));

				//Create request on submittion of work plan - start
				String strRevStDateValue=objRequest
				.getParameter(strAcStDt);
				String strPlStDateValue=objRequest
				.getParameter(strPlStDt);
				String strRevClosureDateValue=objRequest
				.getParameter(strActEnDt);
				String strPlClosureDateValue=objRequest
				.getParameter(strPlEnDt);
				String strEffortValue=objRequest
				.getParameter(strRem);
				String strWorkItemValue=objRequest
				.getParameter(strAct);


				SQAReviewbean sqaReviewbean=new SQAReviewbean();
				sqaReviewbean.setStrProjectId(strProjectId);
				sqaReviewbean.setStrQTL(strUser);
				sqaReviewbean.setStrSQATL(strSQATL);

				if("".equals(strRevStDateValue))
				{
					sqaReviewbean.setStrStDate(strPlStDateValue);
				}
				else
				{
					sqaReviewbean.setStrStDate(strRevStDateValue);
				}
				if("".equals(strRevClosureDateValue))
				{
				sqaReviewbean.setStrEndDate(strPlClosureDateValue);
				}
				else
				{
				sqaReviewbean.setStrEndDate(strRevClosureDateValue);
				}
				sqaReviewbean.setStrEffort(strEffortValue);
				sqaReviewbean.setStrWorkDesc(strWorkItemValue);
				sqaReviewbean.setStrPlan("Planned");
				sqaReviewbean.setStrDn("Yes");
				strRqStatus="Save";
				strRevReqStatus="Saved";

				String strServiceReqId=createReviewDao.checkPlan(strProjectId,strWorkItemValue);
				if(("".equals(strServiceReqId)) && (!"".equalsIgnoreCase(strWorkItemValue)))
				{
				createReviewDao.createReview(sqaReviewbean,strUser,strRqStatus,strRevReqStatus);
				}
				else
				{
				sqaReviewbean.setStrSerReqId(strServiceReqId);
				if((!"Select".equalsIgnoreCase(strWorkItemValue)) && (!"".equalsIgnoreCase(strWorkItemValue)))
				{
			    createReviewDao.updateReview(sqaReviewbean,strUser,strRqStatus,strRevReqStatus);
				}
				}
				//Create request on submittion of work plan - end


			}

			String strBtn = objRequest.getParameter("hdButton");
			if ("Save".equalsIgnoreCase(strBtn)) {
				strResult = objWpDao.createWp(objWrkPlan);
			} else {
				strResult = objWpDao.updateWp(objWrkPlan);
			}

		} catch (Exception e) {

		}
		return strResult;
	}

	public String createWeeklyPlan(HttpServletRequest objRequest,
			HttpServletResponse objResponse) {
		WeeklyPlanBean objWeeklyPlanBean = new WeeklyPlanBean();
		NewWeeklyPlanDao objDAO = new NewWeeklyPlanDao();
		CreateReviewDao createReviewDao=new CreateReviewDao();
		int intRCount = Integer.parseInt(objRequest.getParameter("hdnRCount"));
		SQAReviewbean sqaReviewbean=null;
		String strResult = "";
		String strResult1 = "";
		objWeeklyPlanBean.setStrProjId(objRequest.getParameter("cbProjectId"));
		objWeeklyPlanBean.setStrSqaTl(objRequest.getParameter("cbSQATL"));
		objWeeklyPlanBean.setRCount(intRCount);
		objWeeklyPlanBean.setArlActivity(new ArrayList());
		String strWorkItem = "";
		String strPlStDate = "";
		String strPlClosureDate = "";
		String strRevStDate = "";
		String strRevClosureDate = "";
		String strContactPerson = "";
		String strEffort = "";
		String strTechnology = "";
		String strRqStatus="";
		String strRevReqStatus="";

		HttpSession session = objRequest.getSession(true);
		String strBtn = (String) session.getAttribute("sessionBtn");
		String strStatus = "";
		String strProjectId = objRequest.getParameter("cbProjectId");
		String strUser = (String) session.getAttribute("userId");
		String strSQATL = objRequest.getParameter("cbSQATL");
		String strWkDays = objRequest.getParameter("cbWkDays");

		String[] strWeekdays = strWkDays.split("-");
		//String[] strWeekdays = SQAGeneric.split(strWkDays, "-");
		String strStartDate = strWeekdays[0];
		String strEndDate = strWeekdays[1];

		try {
			for (int i = 0; i < intRCount; i++) {

				strWorkItem = "tbWorkItem" + i;
				strPlStDate = "tbPlannedStdate" + i;
				strPlClosureDate = "tbPlannedClDate" + i;
				strRevStDate = "tbRevisedStdate" + i;
				strRevClosureDate = "tbRevisedCldate" + i;
				strContactPerson = "tbcontactPerson" + i;
				strEffort = "tbEffort" + i;
				strTechnology = "tbTechnology" + i;

				objWeeklyPlanBean.addActivity(objRequest
						.getParameter(strWorkItem), objRequest
						.getParameter(strPlStDate), objRequest
						.getParameter(strPlClosureDate), objRequest
						.getParameter(strRevStDate), objRequest
						.getParameter(strRevClosureDate), objRequest
						.getParameter(strContactPerson), objRequest
						.getParameter(strEffort), objRequest
						.getParameter(strTechnology));


                //////////////////addedd by deepa for automatic review submission as draft start///////////////
				String strRevStDateValue=objRequest
				.getParameter(strRevStDate);
				String strPlStDateValue=objRequest
				.getParameter(strPlStDate);
				String strRevClosureDateValue=objRequest
				.getParameter(strRevClosureDate);
				String strPlClosureDateValue=objRequest
				.getParameter(strPlClosureDate);
				String strEffortValue=objRequest
				.getParameter(strEffort);
				String strWorkItemValue=objRequest
				.getParameter(strWorkItem);


				sqaReviewbean=new SQAReviewbean();
				sqaReviewbean.setStrProjectId(strProjectId);
				sqaReviewbean.setStrQTL(strUser);
				sqaReviewbean.setStrSQATL(strSQATL);

				if("".equals(strRevStDateValue))
				{
					sqaReviewbean.setStrStDate(strPlStDateValue);
				}
				else
				{
					sqaReviewbean.setStrStDate(strRevStDateValue);
				}
				if("".equals(strRevClosureDateValue))
				{
				sqaReviewbean.setStrEndDate(strPlClosureDateValue);
				}
				else
				{
				sqaReviewbean.setStrEndDate(strRevClosureDateValue);
				}
				sqaReviewbean.setStrEffort(strEffortValue);
				sqaReviewbean.setStrWorkDesc(strWorkItemValue);
				sqaReviewbean.setStrPlan("Planned");
				sqaReviewbean.setStrDn("Yes");
				strRqStatus="Save";
				strRevReqStatus="Saved";

				String strServiceReqId=createReviewDao.checkPlan(strProjectId,strWorkItemValue);
				if(("".equals(strServiceReqId)) && (!"Select".equalsIgnoreCase(strWorkItemValue)) && (!"".equalsIgnoreCase(strWorkItemValue)))
				{
				createReviewDao.createReview(sqaReviewbean,strUser,strRqStatus,strRevReqStatus);
				}
				else
				{
				sqaReviewbean.setStrSerReqId(strServiceReqId);
				if((!"Select".equalsIgnoreCase(strWorkItemValue)) && (!"".equalsIgnoreCase(strWorkItemValue)))
				{
			    createReviewDao.updateReview(sqaReviewbean,strUser,strRqStatus,strRevReqStatus);
				}
				}
                ///////////added by deepa for automatic review submission as draft end////////////////


			}



			int intSubmitStatus = objDAO.getProject(strProjectId);

			SqaMail objSqaMail = new SqaMail();

			if ((strBtn == "Save") && (intSubmitStatus == 0)) {
				try {

					objSqaMail.WeeklyPlanMail(strProjectId, strSQATL, strUser,
							strStartDate, strEndDate, "Submitted");
				} catch (Exception e) {

				}
				strStatus = "Submit";
				objDAO.createRevision(objWeeklyPlanBean);
				strResult = objDAO.createWp(objWeeklyPlanBean, strStatus,
						strUser);
				}

			else if ((strBtn == "Save") && (intSubmitStatus > 0)) {
				try {

					objSqaMail.WeeklyPlanMail(strProjectId, strSQATL, strUser,
							strStartDate, strEndDate, "Submitted");
				} catch (Exception e) {

				}
				strStatus = "Submit";
				objDAO.createRevision(objWeeklyPlanBean);
				strResult1 = objDAO.updateWp(objWeeklyPlanBean, strStatus,
						strUser, strWkDays);
				strResult = "Saved";

			} else {
				try {

					objSqaMail.WeeklyPlanMail(strProjectId, strSQATL, strUser,
							strStartDate, strEndDate, "Updated");
				} catch (Exception e) {

				}

				strStatus = "Submit";
				objDAO.createRevision(objWeeklyPlanBean);
				strResult = objDAO.updateWp(objWeeklyPlanBean, strStatus,
						strUser, strWkDays);

			}

		} catch (Exception e) {

		}
		return strResult;
	}

	public String shipmentsPlanned(HttpServletRequest objRequest,
			HttpServletResponse objResponse) throws Exception {
		WeeklyStatusBean objWeeklyStatusBean = new WeeklyStatusBean();
		String strResult = "";
		int intTotCount = 0;
		int intRCount = 0;
		intTotCount = Integer.parseInt(objRequest.getParameter("hdnTotCount"));
		for (int j = 0; j < intTotCount; j++) {
			intRCount = Integer.parseInt(objRequest.getParameter("hdnRCount"
					+ j));

			for (int i = 0; i < intRCount; i++) {
				if ((!("Select".equalsIgnoreCase(objRequest
						.getParameter("tbActProjId" + j + "" + i))))
						&& (!("".equalsIgnoreCase(objRequest
								.getParameter("tbActDeliv" + j + "" + i))))) {

					objWeeklyStatusBean.setStrProjId(objRequest
							.getParameter("tbActProjId" + j + "" + i));
					objWeeklyStatusBean.setStrAct(objRequest
							.getParameter("tbActDeliv" + j + "" + i));
					objWeeklyStatusBean.setStrSqaTl(objRequest
							.getParameter("tbActSqaTl" + j + "" + i));
					objWeeklyStatusBean.setStrPlStDt(objRequest
							.getParameter("tbActPlStDt" + j + "" + i));
					objWeeklyStatusBean.setStrPlEnDt(objRequest
							.getParameter("tbActPlEnDt" + j + "" + i));
					objWeeklyStatusBean.setStrReStDt(objRequest
							.getParameter("tbActReStDt" + j + "" + i));
					objWeeklyStatusBean.setStrReEnDt(objRequest
							.getParameter("tbActReEnDt" + j + "" + i));
					objWeeklyStatusBean.setStrRem(objRequest
							.getParameter("tbActRem" + j + "" + i));
					try {
						WeeklyStatusDao objWeeklyStatusDao = new WeeklyStatusDao();
						strResult = objWeeklyStatusDao
								.planShipment(objWeeklyStatusBean);

					} catch (Exception objExp) {
					}
				}
			}
		}
		return strResult;

	}

	public String otherDetPlanned(HttpServletRequest objRequest,
			HttpServletResponse objResponse) throws Exception {
		WeeklyStatusBean objWeeklyStatusBean = new WeeklyStatusBean();
		String strResult = "";
		int intRCount = Integer.parseInt(objRequest.getParameter("hdnRCount"));
		objWeeklyStatusBean
				.setStrActItems(objRequest.getParameter("tbActItem"));
		objWeeklyStatusBean
				.setStrComments(objRequest.getParameter("tbComment"));
		objWeeklyStatusBean.setStrPrepBy(objRequest.getParameter("tbPrepBy"));
		objWeeklyStatusBean.setStrPrepDt(objRequest.getParameter("tbPrepDate"));
		objWeeklyStatusBean.setStrAppBy(objRequest.getParameter("tbApprBy"));
		objWeeklyStatusBean.setStrAppDt(objRequest.getParameter("tbApprDate"));
		for (int i = 0; i < intRCount; i++) {
			if (!(objRequest.getParameter("tbOthTrng" + i).equals(""))) {

				objWeeklyStatusBean.setStrTraining(objRequest
						.getParameter("tbOthTrng" + i));
				objWeeklyStatusBean.setStrPlanned(objRequest
						.getParameter("tbOthPln" + i));
				objWeeklyStatusBean.setStrRevised(objRequest
						.getParameter("tbOthRev" + i));
				objWeeklyStatusBean.setStrActual(objRequest
						.getParameter("tbOthAct" + i));
				objWeeklyStatusBean.setStrNominated(objRequest
						.getParameter("tbOthNom" + i));
				objWeeklyStatusBean.setStrAttended(objRequest
						.getParameter("tbOthAtten" + i));

				try {
					WeeklyStatusDao objWeeklyStatusDao = new WeeklyStatusDao();
					strResult = objWeeklyStatusDao
							.otherDetails(objWeeklyStatusBean);

				} catch (Exception objExp) {
				}
			}
		}

		return strResult;

	}

	public String createReview(HttpServletRequest objRequest,
			HttpServletResponse objResponse, String strDraft) throws Exception {
		SQAReviewbean objSQAReviewbean = new SQAReviewbean();
		String strResult = "";
		String strRqStatus = "";
		String strRevRqStatus = "";

		String strQtlName = objRequest.getParameter("tbQtlName");
		String strSqaTl = objRequest.getParameter("cbSqaTl");
		String strProjId = objRequest.getParameter("cbProjectId");
		objSQAReviewbean
				.setStrProjectId(objRequest.getParameter("cbProjectId"));
		objSQAReviewbean.setStrQTL(objRequest.getParameter("tbQtlName"));
		objSQAReviewbean.setStrSQATL(objRequest.getParameter("cbSqaTl"));
		objSQAReviewbean.setStrStDate(objRequest.getParameter("tbStdate"));
		objSQAReviewbean.setStrEndDate(objRequest.getParameter("tbEndDate"));
		objSQAReviewbean.setStrEffort(objRequest.getParameter("tbEffort"));
		objSQAReviewbean.setStrWorkItem(objRequest.getParameter("cbWorkItem"));
		objSQAReviewbean.setStrWorkDesc(objRequest
				.getParameter("tbWorkItemDesc"));
		objSQAReviewbean.setStrCurrentPhase(objRequest
				.getParameter("cbCurrentPhase"));
		objSQAReviewbean.setStrPeerIRR(objRequest.getParameter("tbPeerIRR"));
		objSQAReviewbean
				.setStrPeerFatal(objRequest.getParameter("tbPeerFatal"));
		objSQAReviewbean
				.setStrPeerMajor(objRequest.getParameter("tbPeerMajor"));
		objSQAReviewbean
				.setStrPeerMinor(objRequest.getParameter("tbPeerMinor"));
		objSQAReviewbean.setStrPeerCosmetic(objRequest
				.getParameter("tbPeerCosmetic"));
		objSQAReviewbean.setStrDn(objRequest.getParameter("tbDeliveryNote"));
		objSQAReviewbean.setStrPeerComments(objRequest
				.getParameter("tbPeerComments"));
		objSQAReviewbean.setStrCustDelDate(objRequest.getParameter("tbCustDate"));
		objSQAReviewbean.setStrSQAReviewDetails(objRequest
				.getParameter("tbReviewDetails"));
		objSQAReviewbean.setStrSampleTaken(objRequest
				.getParameter("tbSampleTaken"));

		objSQAReviewbean.setStrSQAFatal(objRequest.getParameter("tbSQAFatal"));
		objSQAReviewbean.setStrSQAMajor(objRequest.getParameter("tbSQAMajor"));
		objSQAReviewbean.setStrSQAMinor(objRequest.getParameter("tbSQAMinor"));
		objSQAReviewbean.setStrSQACosmetic(objRequest
				.getParameter("tbSQACosmetic"));
		objSQAReviewbean.setStrSQAComments(objRequest
				.getParameter("tbSQAComments"));
		objSQAReviewbean.setStrCycle(objRequest.getParameter("tbCycleNo"));
		objSQAReviewbean.setStrStatus(objRequest.getParameter("cbStatus"));
		objSQAReviewbean.setStrRecptDeliv(objRequest
				.getParameter("tbRecipient"));
		objSQAReviewbean.setStrSerReqId(objRequest
				.getParameter("hdServiceRequestId"));
		String strrequestid = objRequest.getParameter("hdServiceRequestId");
		/* 10/8/2007 - Upgrade 1.0 - Start */
		objSQAReviewbean.setStrSQAEffort(objRequest
				.getParameter("tbSQAEffort"));
		/* 10/8/2007 - Upgrade 1.0 - End*/
		objSQAReviewbean.setStrDnId(objRequest.getParameter(""));


		String strHdStDate = objRequest.getParameter("hdStDate");
		String strHdEnDate = objRequest.getParameter("hdEnDate");
		String strStDate   = objRequest.getParameter("tbStdate");
		String strEnDate   = objRequest.getParameter("tbEndDate");

		strRqStatus = "Submit";
		strRevRqStatus = "Submitted";
		SqaMail objSqaMail = new SqaMail();
		if (strDraft.equals("Draft")) {
			strRqStatus = "Save";
			strRevRqStatus = "Saved";
		}
		String strSerReqId = "";
		try {
			CreateReviewDao objCreateReviewDao = new CreateReviewDao();

			HttpSession session = objRequest.getSession(true);
			String strBtn = (String) session.getAttribute("sesBtn");


			if ((!strrequestid.equalsIgnoreCase("null"))
					&& (strDraft.equals("Draft"))) {

				strResult = objCreateReviewDao.updateReview(objSQAReviewbean,
						strQtlName, strRqStatus, strRevRqStatus);
			} else if ((!strrequestid.equalsIgnoreCase("null"))
					&& (strDraft.equals("Submit"))) {

				strResult = objCreateReviewDao.updateReview(objSQAReviewbean,
						strQtlName, strRqStatus, strRevRqStatus);
				session.setAttribute("StrSerReqId", strrequestid);
			} else {
				if (strBtn.equals("SaveDB")) {

					if((!strHdStDate.equalsIgnoreCase(strStDate)) || (!strHdEnDate.equalsIgnoreCase(strEnDate)))
					{
						objCreateReviewDao.createRevision(objSQAReviewbean);
					}

					strResult = objCreateReviewDao.updateReview(
							objSQAReviewbean, strQtlName, strRqStatus,
							strRevRqStatus);

				} else {
					objSQAReviewbean.setStrPlan("Unplanned");

					strResult = objCreateReviewDao.createReview(
							objSQAReviewbean, strQtlName, strRqStatus,
							strRevRqStatus);

					strSerReqId = objCreateReviewDao.strCheckSerReqId;
					session.setAttribute("StrSerReqId", strSerReqId);

				}
			}
		} catch (Exception objExp) {

		}

		if (!strDraft.equals("Draft")) {
			try {

				objSqaMail.postMail(strProjId, strSqaTl, strQtlName, "",
						"Submitted",strSerReqId);
			} catch (Exception e) {

			}

		}

		return strResult;
	}

	public String createDelivNote(HttpServletRequest objRequest,
			HttpServletResponse objResponse) throws Exception {
		DeliveryNotesBean objDnBean = new DeliveryNotesBean();
		String strResult = "";
		String strBtnName = "";
		String strBtnId = "";
		String strApprDt = objRequest.getParameter("tbApprDt");
		String strInstApp = objRequest.getParameter("cbInstrt");
		String strProjId = objRequest.getParameter("tbProjId");
		String strDnId = objRequest.getParameter("hdBtnDnId");
		objDnBean.setStrProjId(objRequest.getParameter("tbProjId"));
		objDnBean.setStrDelivMech(objRequest.getParameter("tbDelivMec"));
		objDnBean.setStrAttach(objRequest.getParameter("tbAttach"));
		objDnBean.setStrOthAttach(objRequest.getParameter("tbOthAttach"));
		objDnBean.setStrDelivDesc(objRequest.getParameter("taDelivDesc"));
		objDnBean.setStrVirusFree(objRequest.getParameter("cbVirusFree"));
		objDnBean.setStrPrepDt(objRequest.getParameter("tbPrepDt"));
		if (strApprDt == null) {
			objDnBean.setStrApprDt("");
		} else {
			objDnBean.setStrApprDt(strApprDt);
		}
		if (strInstApp == null) {
			objDnBean.setStrInstAppr("Not Applicable");

		} else {
			objDnBean.setStrInstAppr(objRequest.getParameter("cbInstrt"));
		}
		objDnBean.setStrDelivCompl(objRequest.getParameter("cbDelivCom"));
		objDnBean.setStrRem(objRequest.getParameter("taRem"));
		objDnBean.setStrDnId(objRequest.getParameter("hdBtnDnId"));
		strBtnId = objRequest.getParameter("strBtnId");

		HttpSession session = objRequest.getSession(true);
		String strUser = (String) session.getAttribute("userId");
		try {
			DeliveryNotesDao deliveryNotesDao = new DeliveryNotesDao();

			strResult = deliveryNotesDao.deliveryNote(objDnBean, strUser,
					strBtnId);

		} catch (Exception objExp) {

		}
		return strResult;
	}

	public String saveDNKnownIssues(HttpServletRequest objRequest,
			HttpServletResponse objResponse) throws Exception {
		DNKnownIssuesBean dnKnownIssuesBean = new DNKnownIssuesBean();
		String strResult = "";
		int intRCount = 0;
		intRCount = Integer.parseInt(objRequest.getParameter("hdnRCount"));

		for (int i = 0; i < intRCount; i++) {
			dnKnownIssuesBean.setStrIssueDesc(objRequest
					.getParameter("tbIssueDesc" + i));
			dnKnownIssuesBean.setStrImpact(objRequest.getParameter("tbImpact"
					+ i));
			dnKnownIssuesBean.setStrStatus(objRequest.getParameter("tbStatus"
					+ i));
			dnKnownIssuesBean.setStrRaisedBy(objRequest
					.getParameter("tbRaisedBy" + i));
			dnKnownIssuesBean.setStrOwner(objRequest
					.getParameter("tbOwner" + i));
			dnKnownIssuesBean.setStrSqa(objRequest.getParameter("tbSQA" + i));
			dnKnownIssuesBean.setStrOPenedDt(objRequest
					.getParameter("tbOpenedDt" + i));
			dnKnownIssuesBean.setStrToBeClosed(objRequest
					.getParameter("tbToBeClosed" + i));
			dnKnownIssuesBean.setStrClosedDt(objRequest
					.getParameter("tbClosedDt" + i));
			dnKnownIssuesBean.setStrActionTaken(objRequest
					.getParameter("tbActionTaken" + i));
			dnKnownIssuesBean.setStrIssueNo(objRequest.getParameter("tbIssueNo"
					+ i));
			dnKnownIssuesBean.setStrDnId(objRequest.getParameter("hdnDnId"));

			try {
				DNKnownIssuesDao dnKnownIssuesDao = new DNKnownIssuesDao();
				strResult = dnKnownIssuesDao
						.saveDnKnownIssues(dnKnownIssuesBean);

			} catch (Exception objExp) {
			}

		}
		return strResult;
	}

	public String fileUpload(HttpServletRequest objRequest,
			HttpServletResponse objResponse) throws Exception {
		HttpSession session = objRequest.getSession(true);
		ServletContext objApplication = session.getServletContext();
		String filename = "";
		String contentType = "";
		String fileData = "";
		String strLocalFileName = "";
		String strResult = "";
		int startPos = 0;
		int endPos = 0;
		try {
			contentType = objRequest.getContentType();

			if ((contentType != null)
					&& (contentType.indexOf("multipart/form-data") >= 0)) {
				DataInputStream in = new DataInputStream(objRequest
						.getInputStream());
				DataInputStream in1 = in;
				int formDataLength = objRequest.getContentLength();

				byte dataBytes[] = new byte[formDataLength];
				int byteRead = 0;
				int totalBytesRead = 0;
				while (totalBytesRead < formDataLength) {
					byteRead = in1.read(dataBytes, totalBytesRead,
							formDataLength);
					totalBytesRead += byteRead;
				}

				byte[] line = new byte[128];
				if (totalBytesRead < 3) {
					return "Not Uploaded";
					// exit if file length is not sufficiently large
				}

				String boundary = "";
				String s = "";
				int count = 0;
				int pos = 0;

				// loop for extracting boundry of file
				// could also be extracted from request.getContentType()
				do {
					copyByte(dataBytes, line, count, 1);
					// read 1 byte at a time
					count += 1;
					s = new String(line, 0, 1);
					fileData = fileData + s;
					pos = fileData
							.indexOf("Content-Disposition: form-data; name=\"");
					// set the file name
					if (pos != -1)
						endPos = pos;
				} while (pos == -1);
				boundary = fileData.substring(startPos, endPos);

				// loop for extracting filename
				startPos = endPos;
				do {
					// read 1 byte at a time
					copyByte(dataBytes, line, count, 1);
					count += 1;
					s = new String(line, 0, 1);
					fileData = fileData + s;
					// set the file name
					pos = fileData.indexOf("filename=\"", startPos);

					if (pos != -1)
						startPos = pos;
				} while (pos == -1);
				do {
					// read 1 byte at a time
					copyByte(dataBytes, line, count, 1);
					count += 1;
					s = new String(line, 0, 1);
					fileData = fileData + s;
					pos = fileData.indexOf("Content-Type: ", startPos);
					if (pos != -1)
						endPos = pos;
				} while (pos == -1);
				// to eliminate " from start & end
				filename = fileData.substring(startPos + 10, endPos - 3);
				strLocalFileName = filename;
				int index = filename.lastIndexOf("\\");
				if (index != -1)
					filename = filename.substring(index + 1);
				else
					filename = filename;

				// loop for extracting ContentType
				boolean blnNewlnFlag = false;
				startPos = endPos; // added length of "Content-Type: "
				do {
					// read 1 byte at a time
					copyByte(dataBytes, line, count, 1);
					count += 1;
					s = new String(line, 0, 1);
					fileData = fileData + s;
					pos = fileData.indexOf("\n", startPos);
					if (pos != -1) {
						if (blnNewlnFlag == true)
							endPos = pos;
						else {
							blnNewlnFlag = true;
							pos = -1;
						}
					}
				} while (pos == -1);
				contentType = fileData.substring(startPos + 14, endPos);

				// loop for extracting actual file data (any type of file)
				startPos = count + 1;
				do {
					copyByte(dataBytes, line, count, 1);
					count += 1;
					s = new String(line, 0, 1);
					fileData = fileData + s;
					// check for end of file data i.e boundry value
					pos = fileData.indexOf(boundary, startPos);
				} while (pos == -1);
				endPos = count - boundary.length();
				// file data extracted
				// create destination path & save file there
				String appPath = objApplication.getRealPath("/");

				String destFolder = appPath + "\\";
				filename = destFolder + filename;
				FileOutputStream fileOut = new FileOutputStream(filename);
				fileOut.write(dataBytes, startPos, (endPos - startPos));
				fileOut.flush();
				fileOut.close();

				objRequest.setAttribute("fileName", filename);
				if (!("".equals(filename))) {
					strResult = filename;
				} else {
					strResult = "Not Uploaded";
				}
			} else {
				strResult = "Not Uploaded";

			}

		} catch (Exception e) {

		}

		return strResult;
	}




	void copyByte(byte[] fromBytes, byte[] toBytes, int start, int len) {
		for (int i = start; i < (start + len); i++) {
			toBytes[i - start] = fromBytes[i];
		}
	}

}
