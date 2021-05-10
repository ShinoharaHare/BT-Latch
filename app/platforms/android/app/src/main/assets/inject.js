;(function() {
window.onJsBridgeReady = () => {
  window.$native = {
    platform: 'android'
  }
  for (let [module, methods] of Object.entries(_native)) {
        if (module != 'OnJsBridgeReady') {
            window.$native[module] = {}
            for (let [method, func] of Object.entries(methods)) {
                window.$native[module][method] = (...args) => new Promise((resolve, reject) => func(...args, resolve, reject))
            }
        }
  }
}
})();
