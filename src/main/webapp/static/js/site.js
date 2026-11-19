// Validation phía client (Bootstrap): form có class "needs-validation"
(function () {
  document.querySelectorAll('input[data-match]').forEach(function (conf) {
    var origin = document.getElementById('f_' + conf.dataset.match);
    function check() { conf.setCustomValidity(origin && conf.value !== origin.value ? 'no-match' : ''); }
    conf.addEventListener('input', check);
    if (origin) origin.addEventListener('input', check);
  });
  document.querySelectorAll('form.needs-validation').forEach(function (f) {
    f.addEventListener('submit', function (e) {
      if (!f.checkValidity()) { e.preventDefault(); e.stopPropagation(); }
      f.classList.add('was-validated');
    });
  });
  // Xem trước ảnh khi chọn file: <input data-preview="idCuaImg">
  document.querySelectorAll('input[data-preview]').forEach(function (inp) {
    inp.addEventListener('change', function () {
      var img = document.getElementById(inp.dataset.preview);
      if (img && inp.files[0]) img.src = URL.createObjectURL(inp.files[0]);
    });
  });
})();
