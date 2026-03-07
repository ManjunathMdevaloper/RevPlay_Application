if (!window.Toast) {

window.Toast = {

createContainer() {

if (!document.getElementById('revplay-toast-container')) {

const c = document.createElement('div');

c.id = 'revplay-toast-container';

c.style.cssText =
'position:fixed;bottom:110px;right:20px;z-index:9999;pointer-events:none;max-width:320px;';

document.body.appendChild(c);

}

},

show(message, type = 'success') {

this.createContainer();

const container = document.getElementById('revplay-toast-container');

const colors = {
success:'#FF6600',
danger:'#dc3545',
warning:'#e6a817',
info:'#0ea5e9'
};

const icons = {
success:'✅',
danger:'❌',
warning:'⚠️',
info:'ℹ️'
};

const toast = document.createElement('div');

toast.style.cssText =
'background:white;border-left:4px solid ' + colors[type] +
';padding:10px 16px;border-radius:8px;margin-bottom:8px;' +
'box-shadow:0 4px 20px rgba(0,0,0,0.15);font-size:0.9rem;' +
'pointer-events:auto;animation:toastIn 0.3s ease-out';

toast.innerHTML =
'<span style="margin-right:6px">'+icons[type]+'</span><strong>'+message+'</strong>';

container.appendChild(toast);

setTimeout(() => {

toast.style.opacity = '0';

toast.style.transition = 'opacity 0.4s';

setTimeout(() => toast.remove(), 400);

},3000);

}

};

}