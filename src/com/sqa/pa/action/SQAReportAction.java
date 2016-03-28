package com.sqa.pa.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sqa.pa.bean.DataResponseBean;
import com.sqa.pa.dao.SQASurveyDAO;

public class SQAReportAction {
	public SQAReportAction()
    {
    }
	 /**
     * gets responses from dao
     * @return
     * @throws IOException
     */
    public List<DataResponseBean> getResponseReport() throws IOException{
   	 List<DataResponseBean> list=new ArrayList<DataResponseBean>();
   	 SQASurveyDAO objSurveyDAO = new SQASurveyDAO();
   	 list=objSurveyDAO.getDataResponses();
   	   	 return list;
    }
    
    public List getResponseListforExcel() throws IOException{
    	 List list=new ArrayList();
       	 SQASurveyDAO objSurveyDAO = new SQASurveyDAO();
       	 list=objSurveyDAO.getResponseListForExcel();
       	   	 return list;
    }
    
}
