/**
 * RevPlay - Global Music Player & SPA Router
 * Inspired by reference implementation.
 */
// ============================================================
//  PLAYER
// ============================================================
if (!window.Player) {
    window.Player = {
        audio: new Audio(),
        isPlaying: false,
        isShuffle: false,
        repeatMode: 0, // 0=none, 1=all, 2=one
        queue: [],
        currentIndex: -1,
        currentSong: null,
        originalQueue: [],

        init() {
            this.audio.addEventListener('timeupdate', () => this.updateProgress());
            this.audio.addEventListener('ended', () => this.handleEnded());
            this.audio.addEventListener('loadedmetadata', () => this.updateDuration());

            const get = (id) => document.getElementById(id);

            const safe = (id, ev, fn) => { const el = get(id); if (el) el.addEventListener(ev, fn); };

            safe('player-play-pause', 'click', () => this.togglePlay());
            safe('player-prev', 'click', () => this.prev());
            safe('player-next', 'click', () => this.next());
            safe('player-like', 'click', () => this.toggleFavorite());
            safe('player-add-playlist', 'click', (e) => { e.stopPropagation(); this.togglePlaylistDropdown(); });

            // Seek bar
            const seek = get('player-seek-bar');
            if (seek) {
                seek.addEventListener('input', (e) => {
                    this._seeking = true;
                    const pct = e.target.value;
                    e.target.style.setProperty('--seek-before-width', pct + '%');
                    if (this.audio.duration) {
                        const ct = (pct / 100) * this.audio.duration;
                        const el = get('player-current-time');
                        if (el) el.innerText = this.formatTime(ct);
                    }
                });
                seek.addEventListener('change', (e) => {
                    this._seeking = false;
                    if (this.audio.duration) {
                        this.audio.currentTime = (e.target.value / 100) * this.audio.duration;
                    }
                });
            }

            // Volume
            const vol = get('player-volume');
            if (vol) {
                this.audio.volume = parseFloat(vol.value);
                vol.style.setProperty('--seek-before-width', (vol.value * 100) + '%');
                vol.addEventListener('input', (e) => {
                    this.audio.volume = parseFloat(e.target.value);
                    e.target.style.setProperty('--seek-before-width', (e.target.value * 100) + '%');
                    this.updateVolumeIcon();
                });
            }

            const muteBtn = get('player-mute-btn');
            if (muteBtn) {
                muteBtn.addEventListener('click', () => { this.toggleMute(); });
            }

            // Close playlist dropdown when clicking outside
            document.addEventListener('click', (e) => {
                const menu = get('player-playlist-menu');
                if (menu && !menu.contains(e.target) && e.target.id !== 'player-add-playlist') {
                    menu.style.display = 'none';
                }
            });

            // Show player container
            const container = get('global-music-player');
            if (container) container.style.display = 'block';

            // Fetch profile image
            this.fetchProfile();

            // Restore last-played state
            this.loadLastPlayed();

            App.createToastContainer();
        },

        // ---- Profile ----
        async fetchProfile() {
            try {
                const res = await fetch('/profile/api/me');
                if (!res.ok) return;
                const user = await res.json();
                if (user && user.profilePictureUrl) {
                    document.querySelectorAll('img[alt="profile"]').forEach(img => img.src = user.profilePictureUrl);
                }
            } catch (_) { }
        },

        // ---- Queue ----
        buildQueueFromPage() {
            const btns = document.querySelectorAll('[data-song-id]');
            const unique = new Map();
            Array.from(btns).forEach(b => {
                const id = b.getAttribute('data-song-id');
                const url = b.getAttribute('data-audio-url');
                if (id && url && !unique.has(id)) {
                    unique.set(id, {
                        id: id,
                        url: url,
                        title: b.getAttribute('data-title'),
                        artist: b.getAttribute('data-artist'),
                        cover: b.getAttribute('data-cover') || '/images/default-album.svg'
                    });
                }
            });
            return Array.from(unique.values());
        },

        // ---- Play ----
        playSong(songId, contextQueue, skipQueueAssignment = false) {
            if (!skipQueueAssignment) {
                if (contextQueue && contextQueue.length > 0) {
                    this.originalQueue = [...contextQueue];
                    this.queue = this.isShuffle ? this.shuffleArray([...this.originalQueue]) : [...this.originalQueue];
                    this.currentIndex = this.queue.findIndex(s => String(s.id) === String(songId));
                    if (this.currentIndex === -1) this.currentIndex = 0;
                } else {
                    let idx = this.queue.findIndex(s => String(s.id) === String(songId));
                    if (idx === -1) {
                        const pq = this.buildQueueFromPage();
                        if (pq.length > 0) {
                            this.originalQueue = pq;
                            this.queue = this.isShuffle ? this.shuffleArray([...pq]) : [...pq];
                            idx = this.queue.findIndex(s => String(s.id) === String(songId));
                        }
                    }
                    this.currentIndex = idx !== -1 ? idx : 0;
                }
            }

            const song = this.queue[this.currentIndex];
            if (!song || !song.url) { App.showToast('No audio for this track.', 'warning'); return; }

            this.audio.src = song.url;
            this.currentSong = song; // Keep track of current song independently of queue
            this.audio.play()
                .then(() => {
                    this.isPlaying = true;
                    this.updatePlayPauseIcon();
                    this.updatePlayerUI(song);
                    this.recordPlay(song.id);
                    this.checkFavoriteStatus(song.id);
                    this.saveLastPlayed();
                    const container = document.getElementById('global-music-player');
                    if (container) container.style.display = 'block';
                })
                .catch(err => {
                    console.error("Audio play blocked by browser. User interaction needed:", err);
                    App.showToast('Playback blocked. Click "Play" again.', 'warning');
                });
        },

        togglePlay() {
            if (!this.audio.src) return;
            if (this.audio.paused) {
                this.audio.play();
                this.isPlaying = true;
            } else {
                this.audio.pause();
                this.isPlaying = false;
            }
            this.updatePlayPauseIcon();
        },
        // ---- Controls ----
        prev() {
            if (this.queue.length === 0) return;
            if (this.audio.currentTime > 3) {
                this.audio.currentTime = 0;
                return;
            }
            this.currentIndex = (this.currentIndex - 1 + this.queue.length) % this.queue.length;
            const song = this.queue[this.currentIndex];
            this.playSong(song.id, null, true);
        },

        next() {
            if (this.queue.length === 0) return;
            let idx = this.currentIndex + 1;
            if (idx >= this.queue.length) {
                if (this.repeatMode === 1) idx = 0;
                else { this.isPlaying = false; this.updatePlayPauseIcon(); return; }
            }
            this.currentIndex = idx;
            const song = this.queue[this.currentIndex];
            this.playSong(song.id, null, true);
        },

        handleEnded() {
            if (this.repeatMode === 2) {
                this.audio.currentTime = 0;
                this.audio.play();
            } else {
                this.next();
            }
        },

        toggleShuffle() {
            this.isShuffle = !this.isShuffle;
            const btn = document.getElementById('player-shuffle');
            if (btn) {
                btn.style.color = this.isShuffle ? '#FF6600' : '#888';
                btn.title = this.isShuffle ? 'Shuffle ON' : 'Shuffle OFF';
            }
            if (this.queue.length === 0) return;
            const cur = this.queue[this.currentIndex];
            this.queue = this.isShuffle ? this.shuffleArray([...this.originalQueue]) : [...this.originalQueue];
            this.currentIndex = this.queue.findIndex(s => String(s.id) === String(cur.id));
            App.showToast(this.isShuffle ? 'Shuffle ON 🔀' : 'Shuffle OFF');
        },

        toggleRepeat() {
            this.repeatMode = (this.repeatMode + 1) % 3;
            const btn = document.getElementById('player-repeat');
            if (!btn) return;
            const labels = ['🔁', '🔁', '🔂'];
            const colors = ['#888', '#FF6600', '#FF6600'];
            btn.innerHTML = labels[this.repeatMode];
            btn.style.color = colors[this.repeatMode];
            const msgs = ['Repeat OFF', 'Repeat ALL', 'Repeat ONE'];
            App.showToast(msgs[this.repeatMode]);
        },

        shuffleArray(arr) {
            for (let i = arr.length - 1; i > 0; i--) {
                const j = Math.floor(Math.random() * (i + 1));
                [arr[i], arr[j]] = [arr[j], arr[i]];
            }
            return arr;
        },

        // ---- UI Updates ----
        updatePlayPauseIcon() {
            const icon = document.getElementById('play-pause-icon');
            if (icon) icon.innerText = this.isPlaying ? '⏸' : '▶';
        },

        toggleMute() {
            if (this.audio.volume > 0) {
                this._lastVolume = this.audio.volume;
                this.audio.volume = 0;
            } else {
                this.audio.volume = this._lastVolume || 0.8;
            }
            const vol = document.getElementById('player-volume');
            if (vol) {
                vol.value = this.audio.volume;
                vol.style.setProperty('--seek-before-width', (vol.value * 100) + '%');
            }
            this.updateVolumeIcon();
        },

        updateVolumeIcon() {
            const icon = document.getElementById('player-mute-btn');
            if (!icon) return;
            if (this.audio.volume === 0) icon.innerText = '🔇';
            else if (this.audio.volume < 0.5) icon.innerText = '🔉';
            else icon.innerText = '🔊';
        },

        updatePlayerUI(song) {
            const set = (id, val) => { const el = document.getElementById(id); if (el) el.innerText = val || ''; };
            set('player-title', song.title || 'Unknown Title');
            set('player-artist', song.artist || 'Unknown Artist');
            const cover = document.getElementById('player-cover');
            if (cover) {
                cover.src = song.cover || '/images/default-album.svg';
                cover.onerror = () => { cover.src = '/images/default-album.svg'; };
            }
        },

        updateProgress() {
            const { duration, currentTime } = this.audio;
            if (!duration || isNaN(duration)) return;
            const pct = (currentTime / duration) * 100;
            const bar = document.getElementById('player-seek-bar');
            if (bar && !this._seeking) {
                bar.value = pct;
                bar.style.setProperty('--seek-before-width', pct + '%');
                const ct = document.getElementById('player-current-time');
                if (ct) ct.innerText = this.formatTime(currentTime);
            }
        },

        updateDuration() {
            const el = document.getElementById('player-duration');
            if (el) el.innerText = this.formatTime(this.audio.duration);
        },

        formatTime(s) {
            if (!s || isNaN(s)) return '0:00';
            const m = Math.floor(s / 60);
            const sec = Math.floor(s % 60);
            return m + ':' + (sec < 10 ? '0' : '') + sec;
        },

        // ---- History Recording ----
        recordPlay(songId) {
            fetch('/api/stream/' + songId + '/increment', { method: 'POST' }).catch(() => { });
        },

        // ---- Favorites ----
        async toggleFavorite() {
            const song = this.currentSong || this.queue[this.currentIndex];
            if (!song) { App.showToast('Play a song first!', 'warning'); return; }
            try {
                const res = await fetch('/library/like/' + song.id, {
                    method: 'POST',
                    headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }
                });
                if (res.status === 401) { App.showToast('Please log in to use favorites.', 'warning'); return; }
                if (!res.ok) throw new Error('Server error ' + res.status);
                const isLiked = await res.json();
                this.updateFavoriteUI(isLiked);
                App.showToast(isLiked ? '❤️ Added to Favorites!' : 'Removed from Favorites');
            } catch (err) {
                console.error('Favorite error:', err);
                App.showToast('Could not update favorites.', 'danger');
            }
        },

        async checkFavoriteStatus(songId) {
            try {
                const res = await fetch('/library/like/status/' + songId, {
                    headers: { 'Accept': 'application/json' }
                });
                if (!res.ok) return;
                const isLiked = await res.json();
                this.updateFavoriteUI(isLiked);
            } catch (_) { }
        },

        updateFavoriteUI(isLiked) {
            const icon = document.getElementById('like-icon');
            if (icon) {
                icon.innerText = isLiked ? '❤' : '♡';
                icon.style.color = isLiked ? '#FF6600' : '#888';
            }
        },

        // ---- Playlists Dropdown ----
        togglePlaylistDropdown() {
            const menu = document.getElementById('player-playlist-menu');
            if (!menu) return;
            const isVisible = menu.style.display === 'block';
            menu.style.display = isVisible ? 'none' : 'block';
            if (!isVisible) this.loadUserPlaylists();
        },

        async loadUserPlaylists() {
            const listEl = document.getElementById('player-playlist-list');
            if (!listEl) return;
            listEl.innerHTML = '<li class="px-3 py-1 text-muted small">Loading...</li>';
            try {
                const res = await fetch('/library/api/playlists', { headers: { 'Accept': 'application/json' } });
                if (!res.ok) throw new Error('status ' + res.status);
                const playlists = await res.json();
                if (!playlists || playlists.length === 0) {
                    listEl.innerHTML = '<li class="px-3 py-1 text-muted small">No playlists. <a href="/library/playlists">Create one</a></li>';
                } else {
                    listEl.innerHTML = playlists.map(p =>
                        '<li><a class="dropdown-item py-1 small" href="javascript:void(0)" onclick="Player.addSongToPlaylist(' + p.id + ')">📁 ' + p.name + '</a></li>'
                    ).join('');
                }
                listEl.innerHTML += '<li><hr class="dropdown-divider"></li><li><a class="dropdown-item py-1 small text-primary fw-bold" href="/library/playlists">Manage Playlists</a></li>';
            } catch (err) {
                listEl.innerHTML = '<li class="px-3 py-1 text-danger small">Error loading playlists</li>';
            }
        },

        async addSongToPlaylist(playlistId) {
            const song = this.currentSong || this.queue[this.currentIndex];
            if (!song) { App.showToast('Play a song first!', 'warning'); return; }
            try {
                const res = await fetch('/library/api/playlists/' + playlistId + '/add/' + song.id, {
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });
                if (res.ok) {
                    App.showToast('✅ Added to playlist!');
                    document.getElementById('player-playlist-menu').style.display = 'none';
                } else {
                    App.showToast('Failed to add to playlist.', 'danger');
                }
            } catch (_) {
                App.showToast('Failed to add to playlist.', 'danger');
            }
        },

        // ---- State Persistence ----
        saveLastPlayed() {
            const song = this.queue[this.currentIndex];
            if (!song) return;
            localStorage.setItem('revplay_last', JSON.stringify({
                song: this.currentSong || this.queue[this.currentIndex],
                time: this.audio.currentTime,
                paused: this.audio.paused,
                volume: this.audio.volume
            }));
        },

        loadLastPlayed() {
            try {
                const raw = localStorage.getItem('revplay_last');
                if (!raw) return;
                const state = JSON.parse(raw);
                if (!state || !state.song || !state.song.url) return;

                const song = state.song;
                this.queue = [song];
                this.originalQueue = [song];
                this.currentIndex = 0;
                this.updatePlayerUI(song);
                this.checkFavoriteStatus(song.id);

                this.audio.src = song.url;
                if (state.volume !== undefined) this.audio.volume = state.volume;
                const vol = document.getElementById('player-volume');
                if (vol) {
                    vol.value = this.audio.volume;
                    vol.style.setProperty('--seek-before-width', (vol.value * 100) + '%');
                }

                this.audio.addEventListener('loadedmetadata', () => {
                    if (state.time > 0) this.audio.currentTime = state.time;
                    // Don't auto-play on restore - let user decide
                    this.updatePlayPauseIcon();
                }, { once: true });

                const container = document.getElementById('global-music-player');
                if (container) container.style.display = 'block';
            } catch (_) { }
        },

        // Save state whenever audio time updates (throttled)
        _saveTimer: null,
        scheduleStateSave() {
            clearTimeout(this._saveTimer);
            this._saveTimer = setTimeout(() => this.saveLastPlayed(), 2000);
        }
    };
} // end Player check

// ============================================================
//  APP - SPA Navigation
// ============================================================
if (!window.App) {
    window.App = {
        initialized: false,
        init() {
            if (this.initialized) {
                // Re-show player if it got hidden by DOM replacement
                const container = document.getElementById('global-music-player');
                if (container) container.style.display = 'block';
                return;
            }
            this.initialized = true;
            // Intercept all internal link clicks
            document.addEventListener('click', (e) => {
                const link = e.target.closest('a[href]');
                if (!link) return;
                const href = link.getAttribute('href');
                if (!href || href.startsWith('#') || href.startsWith('javascript') || href.startsWith('mailto')) return;
                if (link.hasAttribute('target') || link.hasAttribute('download')) return;

                const url = new URL(href, window.location.origin);
                if (url.origin !== window.location.origin) return;

                // Skip logout and auth pages (they need full reload)
                const skip = ['/logout', '/login', '/register', '/forgot'];
                if (skip.some(s => url.pathname.startsWith(s))) return;

                e.preventDefault();
                this.navigate(url.href);
            });

            // Browser back/forward
            window.addEventListener('popstate', () => {
                this.loadContent(window.location.href);
            });

            // Intercept form submits
            document.addEventListener('submit', async (e) => {
                if (e.defaultPrevented) return; // Support onsubmit="return confirmAction(...)"
                const form = e.target;
                if (form.hasAttribute('data-no-spa')) return;
                const action = form.getAttribute('action') || window.location.href;
                const skip = ['/logout', '/login', '/register'];
                if (skip.some(s => action.includes(s))) return;
                e.preventDefault();
                await this.handleFormSubmit(form, action);
            });

            // Global Event Delegation for Dynamic Elements
            document.addEventListener('click', async (e) => {
                const likeBtn = e.target.closest('.like-btn');
                if (likeBtn) {
                    e.preventDefault();
                    const songId = likeBtn.getAttribute('data-id') || likeBtn.getAttribute('th:data-id');
                    if (!songId) return;
                    try {
                        const res = await fetch(`/library/like/${songId}`, {
                            method: 'POST',
                            headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }
                        });
                        if (res.ok) {
                            const isLiked = await res.json();
                            if (window.location.pathname.includes('/library/liked') && !isLiked) {
                                const row = likeBtn.closest('tr');
                                if (row) row.style.display = 'none';
                            }
                            this.showToast(isLiked ? '❤️ Added to Favorites!' : 'Removed from Favorites');
                            // Also update player UI if this is the currently playing song
                            if (Player.currentSong && Player.currentSong.id == songId) {
                                Player.updateFavoriteUI(isLiked);
                            }
                        }
                    } catch (err) {
                        console.error('Like error:', err);
                    }
                    return;
                }
            });

            this.createToastContainer();
            Player.init();
        },

        async navigate(url) {
            if (url === window.location.href) {
                await this.loadContent(url);
            } else {
                history.pushState(null, '', url);
                await this.loadContent(url);
            }
        },

        async loadContent(url) {
            try {
                const res = await fetch(url, { headers: { 'X-Requested-With': 'fetch' } });

                // Auth redirect
                if (res.redirected && res.url.includes('/login')) {
                    window.location.href = res.url;
                    return;
                }

                const text = await res.text();
                this.updateDOM(text, res.url);
            } catch (err) {
                console.error('SPA navigation error:', err);
                window.location.href = url;
            }
        },

        async handleFormSubmit(form, action) {
            try {
                const formData = new FormData(form);
                const method = (form.method || 'POST').toUpperCase();
                let response;
                if (method === 'GET') {
                    const params = new URLSearchParams(formData);
                    response = await fetch(action.split('?')[0] + '?' + params, { method: 'GET', headers: { 'X-Requested-With': 'fetch' } });
                } else {
                    response = await fetch(action, { method, body: formData, headers: { 'X-Requested-With': 'fetch' } });
                }

                // Update URL in bar if redirected
                if (response.redirected && response.url !== window.location.href) {
                    history.pushState(null, '', response.url);
                }

                const ct = response.headers.get('content-type') || '';
                if (ct.includes('application/json')) {
                    const data = await response.json();
                    if (data.message) this.showToast(data.message);
                    this.navigate(window.location.href);
                } else {
                    // If it's HTML, update content directly to avoid double fetch
                    const text = await response.text();
                    this.updateDOM(text, response.url);
                }
            } catch (err) {
                console.error('Form submit error:', err);
                // Fallback to native if not a simple network error
                if (!navigator.onLine) this.showToast('No internet connection.', 'danger');
                else form.submit();
            }
        },

        // Extracted DOM update logic to reuse
        updateDOM(htmlText, url) {
            // Forcefully cleanup any leftover Bootstrap modal states that might cause "blur" or "lock"
            document.body.classList.remove('modal-open');
            const backdrops = document.querySelectorAll('.modal-backdrop');
            backdrops.forEach(b => b.remove());
            document.body.style.overflow = '';
            document.body.style.paddingRight = '';

            // If the global confirm modal is still visible or in transition, hide it immediately
            const modalEl = document.getElementById('globalConfirmModal');
            if (modalEl && window.bootstrap) {
                const modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();
            }

            const parser = new DOMParser();
            const doc = parser.parseFromString(htmlText, 'text/html');
            const newMain = doc.getElementById('main-content');
            const curMain = document.getElementById('main-content');

            if (newMain && curMain) {
                curMain.replaceWith(newMain);
                document.title = doc.title || 'RevPlay';
                this.updateActiveNavLink(url || window.location.href);

                // Convert any alerts in the new content to toasts immediately
                if (typeof showToastsFromAlerts === 'function') {
                    showToastsFromAlerts();
                }

                newMain.querySelectorAll('script').forEach(old => {
                    const s = document.createElement('script');
                    Array.from(old.attributes).forEach(a => s.setAttribute(a.name, a.value));
                    s.textContent = old.textContent;
                    old.replaceWith(s);
                });
                // We intentionally do NOT rebuild the Player queue here anymore.
                // That way, whatever queue the user started listening to (e.g. from Playlists) 
                // persists undisturbed while they navigate to other pages naturally!
            } else {
                if (url) window.location.href = url;
            }
        },

        updateActiveNavLink(url) {
            const path = new URL(url, window.location.origin).pathname;
            document.querySelectorAll('.navbar-nav .nav-link').forEach(a => {
                const linkPath = new URL(a.href, window.location.origin).pathname;
                a.classList.toggle('fw-bold', linkPath === path || (linkPath !== '/' && path.startsWith(linkPath)));
            });
        },

        // ---- Confirm Modal ----
        createToastContainer() {
            if (!document.getElementById('revplay-toast-container')) {
                const c = document.createElement('div');
                c.id = 'revplay-toast-container';
                c.style.cssText = 'position:fixed;bottom:110px;right:20px;z-index:9999;pointer-events:none;max-width:320px;';
                document.body.appendChild(c);
            }
        },

        showToast(message, type) {
            type = type || 'success';
            this.createToastContainer();
            const container = document.getElementById('revplay-toast-container');
            const colors = { success: '#FF6600', danger: '#dc3545', warning: '#e6a817', info: '#0ea5e9' };
            const icons = { success: '✅', danger: '❌', warning: '⚠️', info: 'ℹ️' };
            const toast = document.createElement('div');
            toast.style.cssText = [
                'background:white',
                'border-left:4px solid ' + (colors[type] || '#FF6600'),
                'color:#333',
                'padding:10px 16px',
                'border-radius:8px',
                'margin-bottom:8px',
                'min-width:240px',
                'max-width:300px',
                'box-shadow:0 4px 20px rgba(0,0,0,0.15)',
                'font-size:0.9rem',
                'pointer-events:auto',
                'animation:toastIn 0.3s ease-out'
            ].join(';');
            toast.innerHTML = '<span style="margin-right:6px">' + (icons[type] || '✅') + '</span><strong>' + message + '</strong>';
            container.appendChild(toast);
            setTimeout(() => {
                toast.style.opacity = '0';
                toast.style.transition = 'opacity 0.4s';
                setTimeout(() => toast.remove(), 400);
            }, 3000);
        }
    };
} // end App check

// ---- Confirm Modal (form onsubmit helper) ----
window.confirmAction = function (message, formOrCallback) {
    const modalEl = document.getElementById('globalConfirmModal');
    if (!modalEl) {
        if (confirm(message)) {
            if (typeof formOrCallback === 'function') formOrCallback();
            else if (formOrCallback && formOrCallback.tagName === 'FORM') formOrCallback.submit();
        }
        return false;
    }
    const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
    document.getElementById('confirmModalMessage').innerText = message;

    // Dynamic Title
    const titleEl = document.getElementById('confirmModalTitle');
    if (titleEl) {
        let title = 'Confirm Action';
        const msg = message.toLowerCase();
        if (msg.includes('delete') || msg.includes('erase') || msg.includes('remove')) {
            if (msg.includes('album')) title = 'Delete Album?';
            else if (msg.includes('song') || msg.includes('track')) title = 'Delete Song?';
            else if (msg.includes('playlist')) title = 'Delete Playlist?';
            else if (msg.includes('history')) title = 'Clear history?';
            else title = 'Confirm Delete';
        }
        titleEl.innerText = title;
    }

    const old = document.getElementById('confirmModalConfirmBtn');
    const btn = old.cloneNode(true);
    old.parentNode.replaceChild(btn, old);

    const msg = message.toLowerCase();
    const isDanger = /delete|erase|remove|danger|warning/i.test(msg);

    if (isDanger) {
        btn.className = 'btn btn-danger flex-grow-1 rounded-pill fw-bold py-2 shadow-sm';
        if (msg.includes('history')) btn.innerText = 'Clear';
        else if (msg.includes('remove') && !msg.includes('delete')) btn.innerText = 'Remove';
        else btn.innerText = 'Delete';
    } else {
        btn.className = 'btn btn-primary flex-grow-1 rounded-pill fw-bold py-2 shadow-sm';
        btn.innerText = 'Confirm';
    }

    btn.addEventListener('click', () => {
        modal.hide();
        setTimeout(() => {
            if (typeof formOrCallback === 'function') {
                formOrCallback();
            } else if (formOrCallback && formOrCallback.tagName === 'FORM') {
                if (window.App && typeof window.App.handleFormSubmit === 'function') {
                    window.App.handleFormSubmit(formOrCallback, formOrCallback.action || window.location.href);
                } else {
                    formOrCallback.submit();
                }
            }
        }, 300); // slight delay for modal hide animation
    });
    modal.show();
    return false;
};

// ---- Global helpers ----
window.playGlobalAudio = function (id, url, title, artist, cover) {
    const queue = Player.buildQueueFromPage();
    if (queue.length > 0) {
        Player.playSong(id, queue);
    } else {
        Player.playSong(id, [{ id, url, title, artist, cover: cover || '/images/default-album.svg' }]);
    }
};

window.playModalAudio = function (id) {
    if (window.currentModalSongs && window.currentModalSongs.length > 0) {
        Player.playSong(id, window.currentModalSongs);
    }
};

// Animations
(function () {
    const s = document.createElement('style');
    s.textContent = '@keyframes toastIn{from{transform:translateX(20px);opacity:0}to{transform:translateX(0);opacity:1}}';
    document.head.appendChild(s);
})();

// Auto-convert standard alerts to toasts
function showToastsFromAlerts() {
    document.querySelectorAll('.alert:not(.d-none)').forEach(alert => {
        const type = alert.classList.contains('alert-success') ? 'success' :
            (alert.classList.contains('alert-danger') ? 'danger' :
                (alert.classList.contains('alert-warning') ? 'warning' : 'info'));
        if (window.App && window.App.showToast) {
            window.App.showToast(alert.innerText.trim(), type);
            alert.remove();
        }
    });
}

if (document.readyState === 'complete' || document.readyState === 'interactive') {
    App.init();
    showToastsFromAlerts();
} else {
    document.addEventListener('DOMContentLoaded', () => {
        App.init();
        showToastsFromAlerts();
    });
}

// Listen for SPA updates
document.addEventListener('click', () => {
    // Slight timeout to catch SPA DOM changes
    setTimeout(showToastsFromAlerts, 500);
});