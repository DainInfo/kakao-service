<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>우편발송 신청</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: flex-start;
            padding: 40px 20px;
            background-color: #f5f5f5;
        }
        h1 {
            color: #333;
        }
        #description {
            max-width: 650px;
            text-align: center;
            margin: 20px 0 30px 0;
            font-size: 16px;
            color: #555;
            line-height: 1.5;
            background-color: #fff;
            padding: 15px 20px;
            border-radius: 8px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
        }
        button {
            margin: 10px;
            padding: 15px 30px;
            font-size: 16px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: 0.3s;
        }
        #yesBtn { background-color: #4CAF50; color: white; }
        #noBtn { background-color: #f44336; color: white; }
        button:hover { opacity: 0.8; }
        #result {
            margin-top: 20px;
            font-size: 20px;
            font-weight: bold;
            color: #333;
        }
        #info {
            margin-top: 30px;
            font-size: 18px;
            color: #555;
        }
    </style>
</head>
<body>

<%
    String seq = request.getParameter("Seq");
    if (seq != null) {
        session.setAttribute("pendingSeq", seq);
    }
%>
    <h1>우편발송 신청 안내</h1>

    <div id="description">
     
        	'예'를 선택하시면 과태료 고지서가 우편으로 발송됩니다. <br><br>
       		우편 발송 신청 기간은 <strong>11.18 </strong>까지이며, 신청기간이 지난 후에는<br> 서울특별시   대기정책과(☎02-2133-3652, 3658, 3668)로 연락주시기 바랍니다.
    </div>

    <div>
    	<form method="post" action="confirm.jsp">
        	<button name="answer" value="yes" id="yesBtn" type="submit">예</button>
        	<button name="answer" value="no" id="noBtn" type="submit">아니오</button>
        </form>
    </div>

    <div id="result"></div>


   </body>
</html>