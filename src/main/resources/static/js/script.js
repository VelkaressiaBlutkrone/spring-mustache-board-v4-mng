(function () {
  // GlobalExceptionHandler에서 전달된 메시지를 alert로 표시
  const globalMsgEl = document.getElementById("globalMessageData");
  if (globalMsgEl) {
    const msg = globalMsgEl.getAttribute("data-message");
    if (msg) {
      alert(msg);
    }
  }

  const replyForm = document.querySelector("#replyForm");
  const commentTextarea = document.querySelector("#commentTextarea");

  if (replyForm && commentTextarea) {
    const isLoggedIn = replyForm.dataset.isLogin === "true";

    if (!isLoggedIn) {
      commentTextarea.addEventListener("focus", function (e) {
        e.preventDefault();
        commentTextarea.blur();
        alert("로그인이 되었을 때만 입력이 가능합니다.");
      });

      commentTextarea.addEventListener("click", function (e) {
        e.preventDefault();
        commentTextarea.blur();
        alert("로그인이 되었을 때만 입력이 가능합니다.");
      });

      replyForm.addEventListener("submit", function (e) {
        e.preventDefault();
        alert("로그인이 되었을 때만 입력이 가능합니다.");
        return false;
      });
    }
  }
})();
