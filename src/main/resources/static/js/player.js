// player.js

if (!window.Player) {

window.Player = {

audio: new Audio(),

isPlaying:false,
isShuffle:false,
repeatMode:0,

queue:[],
originalQueue:[],
currentIndex:-1,
currentSong:null,

init(){

this.audio.addEventListener('timeupdate',()=>this.updateProgress());
this.audio.addEventListener('ended',()=>this.handleEnded());
this.audio.addEventListener('loadedmetadata',()=>this.updateDuration());

on('player-play-pause','click',()=>this.togglePlay());
on('player-prev','click',()=>this.prev());
on('player-next','click',()=>this.next());
on('player-like','click',()=>this.toggleFavorite());

const seek=$id('player-seek-bar');

if(seek){

seek.addEventListener('input',(e)=>{

this._seeking=true;

if(this.audio.duration){

const ct=(e.target.value/100)*this.audio.duration;

safeText('player-current-time',formatTime(ct));

}

});

seek.addEventListener('change',(e)=>{

this._seeking=false;

if(this.audio.duration){

this.audio.currentTime=(e.target.value/100)*this.audio.duration;

}

});

}

const vol=$id('player-volume');

if(vol){

this.audio.volume=parseFloat(vol.value);

vol.addEventListener('input',(e)=>{

this.audio.volume=parseFloat(e.target.value);

this.updateVolumeIcon();

});

}

this.loadLastPlayed();

},

playSong(songId,contextQueue,skipQueue=false){

if(!skipQueue){

if(contextQueue && contextQueue.length){

this.originalQueue=[...contextQueue];

this.queue=this.isShuffle?shuffleArray([...contextQueue]):[...contextQueue];

this.currentIndex=this.queue.findIndex(s=>String(s.id)===String(songId));

}

}

const song=this.queue[this.currentIndex];

if(!song || !song.url){

Toast.show('No audio for this track','warning');

return;

}

this.audio.src=song.url;

this.currentSong=song;

this.audio.play()

.then(()=>{

this.isPlaying=true;

this.updatePlayPauseIcon();

this.updatePlayerUI(song);

this.saveLastPlayed();

})

.catch(()=>{

Toast.show('Playback blocked. Click play again','warning');

});

},

togglePlay(){

if(!this.audio.src) return;

if(this.audio.paused){

this.audio.play();

this.isPlaying=true;

}else{

this.audio.pause();

this.isPlaying=false;

}

this.updatePlayPauseIcon();

},

next(){

if(!this.queue.length) return;

this.currentIndex=(this.currentIndex+1)%this.queue.length;

const song=this.queue[this.currentIndex];

this.playSong(song.id,null,true);

},

prev(){

if(!this.queue.length) return;

this.currentIndex=(this.currentIndex-1+this.queue.length)%this.queue.length;

const song=this.queue[this.currentIndex];

this.playSong(song.id,null,true);

},

handleEnded(){

if(this.repeatMode===2){

this.audio.currentTime=0;

this.audio.play();

}else{

this.next();

}

},

updatePlayPauseIcon(){

const icon=$id('play-pause-icon');

if(icon) icon.innerText=this.isPlaying?'⏸':'▶';

},

updateVolumeIcon(){

const icon=$id('player-mute-btn');

if(!icon) return;

if(this.audio.volume===0) icon.innerText='🔇';

else if(this.audio.volume<0.5) icon.innerText='🔉';

else icon.innerText='🔊';

},

updatePlayerUI(song){

safeText('player-title',song.title || 'Unknown Title');
safeText('player-artist',song.artist || 'Unknown Artist');

const cover=$id('player-cover');

if(cover){

cover.src=song.cover || '/images/default-album.svg';

cover.onerror=()=>cover.src='/images/default-album.svg';

}

},

updateProgress(){

const {duration,currentTime}=this.audio;

if(!duration || isNaN(duration)) return;

const pct=(currentTime/duration)*100;

const bar=$id('player-seek-bar');

if(bar && !this._seeking){

bar.value=pct;

safeText('player-current-time',formatTime(currentTime));

}

},

updateDuration(){

safeText('player-duration',formatTime(this.audio.duration));

},

saveLastPlayed(){

const song=this.currentSong;

if(!song) return;

localStorage.setItem('revplay_last',JSON.stringify({

song,
time:this.audio.currentTime,
volume:this.audio.volume

}));

},

loadLastPlayed(){

try{

const raw=localStorage.getItem('revplay_last');

if(!raw) return;

const state=JSON.parse(raw);

if(!state.song) return;

this.queue=[state.song];

this.originalQueue=[state.song];

this.currentIndex=0;

this.updatePlayerUI(state.song);

this.audio.src=state.song.url;

if(state.volume) this.audio.volume=state.volume;

}catch(e){}

}

};

}