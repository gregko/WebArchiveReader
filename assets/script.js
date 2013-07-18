function atVoiceAddLoadEvent(func) {
  var oldonload = window.onload;
  if (typeof window.onload != 'function') {
    window.onload = func;
  } else {
    window.onload = function() {
      if (oldonload) {
        oldonload();
      }
      func();
    }
  }
}

atVoiceAddLoadEvent(function() {
  console.log('hl=' + document.getElementsByTagName('html')[0].innerHTML);
});