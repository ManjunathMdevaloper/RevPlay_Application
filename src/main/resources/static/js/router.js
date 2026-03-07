if(!window.App){

window.App={

initialized:false,

init(){

if(this.initialized) return;

this.initialized=true;

document.addEventListener('click',(e)=>{

const link=e.target.closest('a[href]');

if(!link) return;

const href=link.getAttribute('href');

if(!href || href.startsWith('#')) return;

const url=new URL(href,window.location.origin);

if(url.origin!==window.location.origin) return;

e.preventDefault();

this.navigate(url.href);

});

window.addEventListener('popstate',()=>{

this.loadContent(window.location.href);

});

},

async navigate(url){

history.pushState(null,'',url);

await this.loadContent(url);

},

async loadContent(url){

try{

const res=await fetch(url,{headers:{'X-Requested-With':'fetch'}});

const text=await res.text();

this.updateDOM(text,url);

}catch(err){

window.location.href=url;

}

},

updateDOM(html,url){

const parser=new DOMParser();

const doc=parser.parseFromString(html,'text/html');

const newMain=doc.getElementById('main-content');

const curMain=document.getElementById('main-content');

if(newMain && curMain){

curMain.replaceWith(newMain);

document.title=doc.title || 'RevPlay';

}

}

};

}