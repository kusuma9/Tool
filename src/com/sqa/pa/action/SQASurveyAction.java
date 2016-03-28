package com.sqa.pa.action;

import com.sqa.pa.bean.AnswerBean;
import com.sqa.pa.bean.DataResponseBean;
import com.sqa.pa.bean.QuestionBean;
import com.sqa.pa.bean.sqaSurveyBean;
import com.sqa.pa.dao.SQASurveyDAO;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SQASurveyAction 
{

    public SQASurveyAction()
    {
    }
    
   
   
     public void saveFeedback(HttpServletRequest objRequest, HttpServletResponse objResponse, String Dept)
        throws Exception
    {
        sqaSurveyBean objSQAsurveyBean = new sqaSurveyBean();
        SQASurveyDAO objSurveyDAO = new SQASurveyDAO();
        ArrayList qList = objSurveyDAO.getQuestions(Dept);
        Iterator it = qList.iterator();
        QuestionBean qBean = new QuestionBean();
        Map aMap = new HashMap();
        Map feedbackMap = new HashMap();
        Map remarksMap = new HashMap();
        for(; it.hasNext(); remarksMap.put(new Integer(qBean.getQuestionId()), objRequest.getParameter("hdRemarks" + qBean.getQuestionId())))
        {
            qBean = (QuestionBean)it.next();
            aMap.put(new Integer(qBean.getQuestionId()), objRequest.getParameter("cbQuestion" + qBean.getQuestionId()));
        }

        objSQAsurveyBean.setSurveyMap(aMap);
        objSQAsurveyBean.setRemarksMap(remarksMap);
        objSQAsurveyBean.setProjectRole(objRequest.getParameter("cbRole"));
        String strTemp = "";
        if("GAM".equals(objRequest.getParameter("cbRole")))
        {
            String temp[] = objRequest.getParameterValues("cbAccount");
            for(int i = 0; i < temp.length; i++)
                strTemp = strTemp + temp[i] + ",";

            strTemp = strTemp.substring(0, strTemp.length() - 1);
            objSQAsurveyBean.setAccount(strTemp);
        } else
        {
            objSQAsurveyBean.setAccount(objRequest.getParameter("cbAccount"));
        }
        if("VH".equals(objRequest.getParameter("cbRole")))
        {
            objSQAsurveyBean.setVertical("");
            objSQAsurveyBean.setSector(objRequest.getParameter("cbVertical"));
        } else
        if("PMO".equals(objRequest.getParameter("cbRole")))
            objSQAsurveyBean.setVertical("PMO");
        else
        if("AD".equals(objRequest.getParameter("cbRole")))
        {
            objSQAsurveyBean.setVertical("A&D");
            objSQAsurveyBean.setProjectRole("A&D");
        } else
        if("Admin".equals(objRequest.getParameter("cbRole")))
            objSQAsurveyBean.setVertical("Admin");
        else
        if("Infra".equals(objRequest.getParameter("cbRole")))
        {
            objSQAsurveyBean.setVertical("Infra");
        } else
        {
            objSQAsurveyBean.setVertical(objRequest.getParameter("cbVertical"));
            objSQAsurveyBean.setSector("");
        }
        objSQAsurveyBean.setLocation(objRequest.getParameter("cbLocation"));
        objSQAsurveyBean.setExpInKeane(objRequest.getParameter("cbExp"));
        objSQAsurveyBean.setChange1(objRequest.getParameter("tbChange1"));
        objSQAsurveyBean.setChange2(objRequest.getParameter("tbChange2"));
        objSQAsurveyBean.setLike1(objRequest.getParameter("tbLike1"));
        objSQAsurveyBean.setLike2(objRequest.getParameter("tbLike2"));
        objSQAsurveyBean.setNoOfMembers(objRequest.getParameter("cbMembers"));
        objSQAsurveyBean.setDept(Dept);
        objSurveyDAO.saveFeedback(objSQAsurveyBean);
    }
    
     /**
      * This method gets the scores for answers from the DAO.
      * @param location
      * @return
      * @throws Exception
      */
     public Map<String,List<AnswerBean>> getScores(String location) throws Exception{
    	 List<AnswerBean> list1 = new ArrayList<AnswerBean>();
    	 List<AnswerBean> list2= new ArrayList<AnswerBean>();
    	 List<AnswerBean> list3=new ArrayList<AnswerBean>();
    	 List<AnswerBean> list4=new ArrayList<AnswerBean>();
    	 List<AnswerBean> list5=new ArrayList<AnswerBean>();
    	 List<AnswerBean> list6=new ArrayList<AnswerBean>();
    	 List<AnswerBean> list7=new ArrayList<AnswerBean>();
    	 List<AnswerBean> list8=new ArrayList<AnswerBean>();
    	 List<AnswerBean> list9=new ArrayList<AnswerBean>();
    	 List<AnswerBean> list10=new ArrayList<AnswerBean>();
    	 SQASurveyDAO objSurveyDAO = new SQASurveyDAO();
     	 List<sqaSurveyBean> beanList=objSurveyDAO.getScores(location);
     	 Map<String,List<AnswerBean>> map=new HashMap<String,List<AnswerBean>>();
     	 for(int i=0;i<beanList.size();i++){
     		sqaSurveyBean bean1=beanList.get(i);
     		QuestionBean bean=bean1.getQuestionBean();
     		if(bean.getQuestionId().equals("1")){
     			
     			list1.add(bean.getAnswer());
     			
     		}
     		else if(bean.getQuestionId().equals("2")){
     			
     			list2.add(bean.getAnswer());
     		}
     		else if(bean.getQuestionId().equals("3")){
     			
     			list3.add(bean.getAnswer());
     		}
     		else if(bean.getQuestionId().equals("4")){
     			
     			list4.add(bean.getAnswer());
     		}
     		else if(bean.getQuestionId().equals("5")){
     			
     			list5.add(bean.getAnswer());
     		}
     		else if(bean.getQuestionId().equals("6")){
     			
     			list6.add(bean.getAnswer());
     		}
     		else if(bean.getQuestionId().equals("7")){
     			
     			list7.add(bean.getAnswer());
     		}
     		else if(bean.getQuestionId().equals("8")){
     			
     			list8.add(bean.getAnswer());
     		}
     		else if(bean.getQuestionId().equals("9")){
     			
     			list9.add(bean.getAnswer());
     		}
     		else if(bean.getQuestionId().equals("10")){
     			
     			list10.add(bean.getAnswer());
     		}
     		
     		}
     		list1=analyzeList(list1);
     		list2=analyzeList(list2);
     		list3=analyzeList(list3);
     		list4=analyzeList(list4);
     		list5=analyzeList(list5);
     		list6=analyzeList(list6);
     		list7=analyzeList(list7);
     		list8=analyzeList(list8);
     		list10=analyzeList(list10);
     		list9=analyzeList(list9);
     		
     		map.put("SQA spends adequate time to understand the project",list1);
     		map.put("SQA spends adequate time to review artifacts",list2);
     		map.put("SQA resources are adequately trained to perform their role",list3);
     		map.put("SQA review comments reflect an understanding of the project",list4);
     		map.put("SQA team is adequately staffed",list5);
     		map.put("The reports published by SQA are accurate and complete",list6);
     		map.put("SQA team gets along well with the Project team",list7);map.put("SQA enforces adequate process compliance",list8);
     		map.put("SQA provides timely support",list9);
     		map.put("SQA recommends process improvements in projects (Suggestions for new tools,checklists,Templates etc)",list10);
     		return map;
}
    /**
     * In the database,if the count for say answer id 0 is zero then fetched records 
     * are from 1 to 4 instead of 0 to 4 because of which the results are displayed wrong in the UI
     * This method will analyze the list and add zero for answer id whose count is zero.
     * @param list
     * @return
     */
     private List<AnswerBean> analyzeList(List<AnswerBean> list){
    	 Collections.sort(list);
    	 HashMap<String,Integer> map=new HashMap<String,Integer>();
    	 for(AnswerBean bean:list)
    	 {
    		 map.put(bean.getAnswerId(), bean.getCount());
    	 }
    	 
    	 Set set=map.keySet();
    	 boolean b0=set.contains("0");
    	 boolean b1=set.contains("1");
    	 boolean b2=set.contains("2");
    	 boolean b3=set.contains("3");
    	 boolean b4=set.contains("4");
    	 if(b3==true){
    		 if(b0==false && b1==false && b2==false && b4==false){
    			 AnswerBean bean0=new AnswerBean();
    			 bean0.setAnswerId("0");
    			 bean0.setCount(0);
    			 list.add(bean0);
    			 AnswerBean bean1=new AnswerBean();
    			 bean1.setAnswerId("1");
    			 bean1.setCount(0);
    			 list.add(bean1);
    			 AnswerBean bean2=new AnswerBean();
    			 bean2.setAnswerId("2");
    			 bean2.setCount(0);
    			 list.add(bean2);
    			 AnswerBean bean4=new AnswerBean();
    			 bean4.setAnswerId("4");
    			 bean4.setCount(0);
    			 list.add(bean4);
    		 }
    	 }
    	 if(b2==true && b3==true){
    		 if(b0==false && b1==false && b4==false){
    			 AnswerBean bean0=new AnswerBean();
    			 bean0.setAnswerId("0");
    			 bean0.setCount(0);
    			 list.add(bean0);
    			 AnswerBean bean1=new AnswerBean();
    			 bean1.setAnswerId("1");
    			 bean1.setCount(0);
    			 list.add(bean1);
    			 AnswerBean bean4=new AnswerBean();
    			 bean4.setAnswerId("4");
    			 bean4.setCount(0);
    			 list.add(bean4);
    		 }
    	 }
    	 if(b1==true && b2==true && b3==true){
    		 if(b0==false && b4==false){
    			 AnswerBean bean0=new AnswerBean();
    			 bean0.setAnswerId("0");
    			 bean0.setCount(0);
    			 list.add(bean0);
    			 AnswerBean bean4=new AnswerBean();
    			 bean4.setAnswerId("4");
    			 bean4.setCount(0);
    			 list.add(bean4);
    		 }
    		 else if(b4==true && b0==false){
    			 AnswerBean bean0=new AnswerBean();
    			 bean0.setAnswerId("0");
    			 bean0.setCount(0);
    			 list.add(bean0);
    		 }
    		 
    		 
    	 }
    	 if(b0==true && b3==true && b4==true){
    		 if(b1==false && b2==false){
    			 AnswerBean bean1=new AnswerBean();
    			 bean1.setAnswerId("1");
    			 bean1.setCount(0);
    			 list.add(bean1);
    			 AnswerBean bean2=new AnswerBean();
    			 bean2.setAnswerId("2");
    			 bean2.setCount(0);
    			 list.add(bean2);
    		 }
    		 if(b1==true && b2==false){
    			 AnswerBean bean2=new AnswerBean();
    			 bean2.setAnswerId("2");
    			 bean2.setCount(0);
    			 list.add(bean2);
    		 }
    	 }
    	 if(b3==true && b4==true){
    		 if(b0==false && b1==false && b2==false){
    			 AnswerBean bean0=new AnswerBean();
    			 bean0.setAnswerId("0");
    			 bean0.setCount(0);
    			 list.add(bean0);
    			 AnswerBean bean1=new AnswerBean();
    			 bean1.setAnswerId("1");
    			 bean1.setCount(0);
    			 list.add(bean1);
    			 AnswerBean bean2=new AnswerBean();
    			 bean2.setAnswerId("2");
    			 bean2.setCount(0);
    			 list.add(bean2);
    		 }
    	 }
    	 if(b0==true && b2==true && b3==true && b4==true){
    		 if(b1==false){
    			 AnswerBean bean1=new AnswerBean();
    			 bean1.setAnswerId("1");
    			 bean1.setCount(0);
    			 list.add(bean1); 
    		 }
    	 }
    	 if(b2==true && b3==true && b4==true){
    		 if(b0==false && b1==false){
    			 AnswerBean bean0=new AnswerBean();
    			 bean0.setAnswerId("0");
    			 bean0.setCount(0);
    			 list.add(bean0);
    			 AnswerBean bean1=new AnswerBean();
    			 bean1.setAnswerId("1");
    			 bean1.setCount(0);
    			 list.add(bean1); 
    		 }


    	 }
    	 if(b0==true && b1==true && b2==true && b3==true){
    		 if(b4==false){
    			 AnswerBean bean4=new AnswerBean();
    			 bean4.setAnswerId("4");
    			 bean4.setCount(0);
    			 list.add(bean4);
    		 }
    	 }
    	 
    	 if(b0==true && b1==true && b2==true && b4==true){
    		 if(b3==false){
    		 AnswerBean bean3=new AnswerBean();
			 bean3.setAnswerId("3");
			 bean3.setCount(0);
			 list.add(bean3);
    	 }}
    	 if(b0==true && b1==true && b2==true){
    		 if(b3==false && b4==false){
        		 AnswerBean bean3=new AnswerBean();
    			 bean3.setAnswerId("3");
    			 bean3.setCount(0);
    			 AnswerBean bean4=new AnswerBean();
    			 bean4.setAnswerId("4");
    			 bean4.setCount(0);
    			 list.add(bean3);
        	 } 
    	 }
    	 			
    	Collections.sort(list) ;
	return list;
}


	/**
      * Gets the overall response
      * @return
      * @throws IOException
      */
     public List<sqaSurveyBean> getOverallResponse() throws IOException{
    	 List<sqaSurveyBean> list=new ArrayList<sqaSurveyBean>();
    	 SQASurveyDAO objSurveyDAO = new SQASurveyDAO();
    	 list=objSurveyDAO.getOverallResponse();
    	 return list;
     }
     
    
    
}
