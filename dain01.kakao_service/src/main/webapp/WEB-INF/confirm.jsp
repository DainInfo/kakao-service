<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta charset="UTF-8">
  <title>설문 완료</title>
  <style>
    body {
      font-family: "맑은 고딕", sans-serif;
      background-color: #f5f7fa;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      margin: 0;
    }
    .container {
      background: #fff;
      border-radius: 12px;
      box-shadow: 0 4px 10px rgba(0,0,0,0.1);
      padding: 40px;
      text-align: center;
      width: 400px;
    }
    h1 {
      color: #2c3e50;
      margin-bottom: 20px;
    }
    p {
      color: #555;
      font-size: 16px;
      margin-bottom: 30px;
    }
    .btn {
      display: inline-block;
      background: #2c7be5;
      color: #fff;
      text-decoration: none;
      padding: 12px 24px;
      border-radius: 8px;
      font-size: 16px;
      transition: background 0.2s;
    }
    .btn:hover {
      background: #1a5bb8;
    }
  </style>
  
  <style>
  #closeBtn {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    background: #e0e0e0;      /* 연한 회색 */
    color: #444;              /* 진한 회색 글자 */
    border: none;
    padding: 12px 20px;
    font-size: 15px;
    border-radius: 8px;
    cursor: pointer;
    transition: background 0.2s;
  }
  #closeBtn:hover {
     background: #d5d5d5;
     border-color: #b5b5b5;
  }

	</style>
</head>
<body>
	<%
		String seq = (String) session.getAttribute("pendingSeq");
		String answer = (String) request.getParameter("answer");
		System.out.println(seq);
		String msg;
		if (seq == null) {

		} else {

			// 중복 방지를 위해 세션에서 제거
			session.removeAttribute("pendingSeq");
		}

		if (answer.equals("yes")) {
			answer = "예";
			msg = "를 선택하여 과태료 고지서는 우편으로 발송됩니다.";
		} else {
			answer = "아니요";
			msg = "를 선택하여 과태료 고지서는 우편으로 발송되지 않습니다.";
		}
	%>
	
	<div class="container">
    <h1>설문이 완료되었습니다</h1>
    <p>응답해 주셔서 감사합니다.<br>
       설문에서 <strong>“<%= answer %>”</strong><%= msg %></p>
    

    <div>
    	<form method="post" action="index.jsp">
        	<button name="answer" value="close" id="closeBtn" type="submit">창 닫기</button>
        </form>
    </div>
  </div>
</body>
</html>