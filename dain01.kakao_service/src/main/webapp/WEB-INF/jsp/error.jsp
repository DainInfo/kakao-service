<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>


<body class="body">

<div class="error">

    <!-- 아이콘 -->
    <div class="mgt30">
        <img alt="warning" src="/resources/new/images/new/warning.png" width="200">
    </div>

    <!-- 오류코드/오류문구 -->
    <div class="mgt30">
        <p class="code">ID : ${executId}</p>
        <p class="msg">처리중 문제가 발생하였습니다. : ${exception.message}</p>
    </div>

    <!-- 안내사항 -->
    <div class="mgt30">
        <p class="txt">관리자에게 문의 하세요.</p>
        <p class="txt">계속 이 화면이 보이신다면 1588-1234로 문의주시기 바랍니다.</p>
    </div>

    <!-- 이동버튼 -->
    <div class="mgt30">
        <button class="btn_txt_m" onclick="history.back();">[뒤로]</button>
    </div>

</div>

</body>


<script type="text/javaScript">
    $(function () {
        //tiles script.jsp에 넣으면 asis 스타일에 영향을 받으므로 스크립트처리함. (이우성)
        $("head").eq(0).append('<link rel="stylesheet" type="text/css" href="/resources/new/css/common.css">');
    });
</script>