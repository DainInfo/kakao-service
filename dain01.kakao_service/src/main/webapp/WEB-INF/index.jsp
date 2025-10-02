<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>디지털약자 여부 설문</title>
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
    <h1>디지털약자 여부 설문</h1>

    <div id="description">
        <strong>디지털약자란?</strong><br>
        디지털약자란, 디지털 기기와 온라인 서비스를 이용하는 데 어려움을 겪는 사람을 의미합니다.<br><br>
           <strong>설문에서 ‘예’를 선택하신 경우, 부과된 과태료 고지서는 우편으로 송부됩니다.</strong>
    </div>

    <div>
    	<form method="post" action="confirm.jsp">
        	<button id="yesBtn" type="submit">예</button>
        	<button id="noBtn" type="submit">아니오</button>
        </form>
    </div>

    <div id="result"></div>

    <div id="info">
        대기정책과(02-2133-3652, 02-2133-3658, 02-2133-3668)
    </div>

   </body>
</html>