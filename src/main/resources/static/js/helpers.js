window.$id = (id) => document.getElementById(id);

window.on = (id, event, fn) => {
    const el = $id(id);
    if (el) el.addEventListener(event, fn);
};

window.safeText = (id, value = '') => {
    const el = $id(id);
    if (el) el.innerText = value;
};

window.formatTime = (s) => {
    if (!s || isNaN(s)) return '0:00';
    const m = Math.floor(s / 60);
    const sec = Math.floor(s % 60);
    return m + ':' + (sec < 10 ? '0' : '') + sec;
};

window.shuffleArray = (arr) => {
    for (let i = arr.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [arr[i], arr[j]] = [arr[j], arr[i]];
    }
    return arr;
};