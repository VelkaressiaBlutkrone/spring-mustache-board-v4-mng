/**
 * Summernote 에디터 초기화 (게시글 작성/수정 폼)
 */
$(function () {
  var $editor = $("#boardContent");
  if (!$editor.length) return;

  var initialEl = $("#boardContentInitial");
  var initialContent = initialEl.length ? initialEl.html() : "";

  $editor.summernote({
    height: 300,
    placeholder: "내용을 입력하세요.",
    toolbar: [
      ["style", ["style"]],
      ["font", ["bold", "italic", "underline", "strikethrough"]],
      ["fontsize", ["fontsize"]],
      ["color", ["color"]],
      ["para", ["ul", "ol", "paragraph"]],
      ["table", ["table"]],
      ["insert", ["link", "picture"]],
      ["view", ["fullscreen", "codeview"]],
    ],
  });

  if (initialContent) {
    $editor.summernote("code", initialContent);
  }
});
