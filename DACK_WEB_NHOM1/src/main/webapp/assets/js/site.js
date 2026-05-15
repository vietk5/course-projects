/* ElectroMart – Dark ROG micro-interactions */
(function () {
  "use strict";
  const on = (el, evt, fn, opts) => el && el.addEventListener(evt, fn, opts || false);
  const qs = (s, root = document) => root.querySelector(s);
  const qsa = (s, root = document) => Array.from(root.querySelectorAll(s));
  const throttle = (fn, wait = 100) => { let t=0, tm; return (...a)=>{const n=Date.now(); if(n-t>=wait){t=n; fn(...a);} else {clearTimeout(tm); tm=setTimeout(()=>{t=Date.now(); fn(...a);}, wait-(n-t));}}; };
  const reduced = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  if (reduced) document.documentElement.classList.add('reduced-motion');

  // Reveal-in
  (function(){
    const els = qsa('.product-card, .brand-pill, .cat-tile');
    if (!els.length || reduced) return;
    els.forEach(el => el.classList.add('reveal-wait'));
    const io = new IntersectionObserver(es=>{
      es.forEach(e=>{ if(e.isIntersecting){ e.target.classList.add('reveal-in'); io.unobserve(e.target);} });
    }, {threshold:.08});
    els.forEach(el=> io.observe(el));
  })();

  // Navbar shadow
  (function(){
    const nav = qs('.glass-nav'); if(!nav) return;
    const apply = ()=> nav.classList.toggle('scrolled', window.scrollY > 4);
    apply(); on(window, 'scroll', throttle(apply, 80), {passive:true});
  })();

  // Tooltips
  (function(){
    if (!window.bootstrap) return;
    qsa('[data-bs-toggle="tooltip"]').forEach(el => new bootstrap.Tooltip(el));
  })();

  // Smooth anchor
  (function(){
    on(document, 'click', e=>{
      const a = e.target.closest('a[href^="#"]'); if(!a) return;
      const id = a.getAttribute('href'); if(!id || id==='#') return;
      const el = qs(id); if(!el) return;
      e.preventDefault();
      el.scrollIntoView({behavior: reduced ? 'auto' : 'smooth', block:'start'});
    });
  })();

  // Lazy images fallback
  (function(){
    const imgs = qsa('img[data-src]'); if(!imgs.length) return;
    if ('loading' in HTMLImageElement.prototype) {
      imgs.forEach(img => { img.loading='lazy'; img.src=img.dataset.src; img.removeAttribute('data-src'); });
      return;
    }
    const io = new IntersectionObserver(es=>{
      es.forEach(e=>{
        if(e.isIntersecting){
          const img = e.target; img.src = img.dataset.src; img.loading='lazy'; img.removeAttribute('data-src');
          io.unobserve(img);
        }
      });
    }, {rootMargin:'200px 0', threshold:.01});
    imgs.forEach(img=> io.observe(img));
  })();
  // ========== FLASH DEALS ==========

window.ElectroMart = window.ElectroMart || {};

ElectroMart.initFlashRail = function(){
  const wrap = document.getElementById('flashRailWrapper');
  if(!wrap) return;
  const rail = wrap.querySelector('.flash-rail');
  const prev = wrap.querySelector('.flash-btn.prev');
  const next = wrap.querySelector('.flash-btn.next');
  const step = () => Math.max(rail.clientWidth * 0.9, 300);

  prev?.addEventListener('click', () =>
    rail.scrollBy({ left: -step(), behavior: 'smooth' })
  );
  next?.addEventListener('click', () =>
    rail.scrollBy({ left:  step(), behavior: 'smooth' })
  );
};

ElectroMart.startCountdown = function(){
  const cd = document.getElementById('dealsCountdown');
  if(!cd) return;
  const pad = n => String(n).padStart(2,'0');

  // Đếm ngược 10 phút từ bây giờ
  const DURATION_MINUTES = 10;
  const endTime = new Date(Date.now() + DURATION_MINUTES * 60 * 1000);

  function tick(){
    const now = Date.now();
    let remaining = endTime.getTime() - now;
    
    if (remaining <= 0) {
      cd.innerHTML = '<span class="text-warning fs-4">⏰ Đã hết giờ! Đang tải lại...</span>';
      // Tự động reload sau 2 giây
      setTimeout(() => {
        location.reload();
      }, 2000);
      return;
    }
    
    const h = Math.floor(remaining/3.6e6);
    const m = Math.floor((remaining%3.6e6)/6e4);
    const s = Math.floor((remaining%6e4)/1000);
    
    // Hiển thị to hơn với màu nổi bật
    cd.innerHTML = `<span style="font-size: 1.8rem; color: #00ff00; text-shadow: 0 0 10px #00ff00, 0 0 20px #00ff00;">${pad(m)}</span><span style="font-size: 1.5rem; color: #fff;"> : </span><span style="font-size: 1.8rem; color: #00ff00; text-shadow: 0 0 10px #00ff00, 0 0 20px #00ff00;">${pad(s)}</span>`;
  }
  
  tick();
  setInterval(tick, 1000);
};

document.addEventListener('DOMContentLoaded', () => {
  ElectroMart.initFlashRail();
  ElectroMart.startCountdown();
});

})();
// Parallax background (subtle) — updates CSS vars --bgX/--bgY
(function () {
  const root = document.documentElement;
  const reduce = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  if (reduce) return;

  function onMove(e){
    const x = (e.clientX || window.innerWidth/2) / window.innerWidth;
    const y = (e.clientY || 200) / window.innerHeight;
    const bx = 40 + x*20;  // 40% → 60%
    const by = 30 + y*20;  // 30% → 50%
    root.style.setProperty('--bgX', bx.toFixed(2) + '%');
    root.style.setProperty('--bgY', by.toFixed(2) + '%');
  }
  window.addEventListener('mousemove', onMove, {passive:true});
})();

document.addEventListener('DOMContentLoaded', () => {
  const initRail = (rootSel) => {
    const root = document.querySelector(rootSel);
    if (!root) return;

    const track = root.querySelector('.dr-track');
    const prev  = root.querySelector('.dr-nav.prev');
    const next  = root.querySelector('.dr-nav.next');

    const step = () => Math.max(260 * 3, root.clientWidth * 0.9); // cuộn ~3 card hoặc 90% viewport

    const update = () => {
      const max = track.scrollWidth - track.clientWidth - 1;
      prev.disabled = track.scrollLeft <= 0;
      next.disabled = track.scrollLeft >= max;
    };

    prev.addEventListener('click', () => track.scrollBy({ left: -step(), behavior: 'smooth' }));
    next.addEventListener('click', () => track.scrollBy({ left:  step(), behavior: 'smooth' }));
    track.addEventListener('scroll', () => requestAnimationFrame(update));
    window.addEventListener('resize', update);
    update();
  };

  initRail('#hot-deals');
});

// ========== AUTOCOMPLETE SEARCH ==========
(function() {
  'use strict';
  
  // Khởi tạo autocomplete cho search box
  function initAutocomplete() {
    const searchInput = document.querySelector('input[name="q"]');
    if (!searchInput) return;
    
    const contextPath = searchInput.dataset.contextPath || '';
    let autocompleteList = null;
    let currentFocus = -1;
    let debounceTimer = null;
    
    // Tạo dropdown autocomplete
    function createAutocompleteList() {
      if (autocompleteList) return autocompleteList;
      
      autocompleteList = document.createElement('div');
      autocompleteList.className = 'autocomplete-list';
      autocompleteList.style.cssText = `
        position: absolute;
        top: 100%;
        left: 0;
        right: 0;
        background: #1a1a1a;
        border: 1px solid rgba(255, 107, 107, 0.3);
        border-top: none;
        border-radius: 0 0 8px 8px;
        max-height: 300px;
        overflow-y: auto;
        z-index: 1000;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
        display: none;
      `;
      
      searchInput.parentElement.style.position = 'relative';
      searchInput.parentElement.appendChild(autocompleteList);
      return autocompleteList;
    }
    
    // Fetch suggestions từ API
    function fetchSuggestions(query) {
      if (query.length < 2) {
        hideAutocomplete();
        return;
      }
      
      fetch(`${contextPath}/api/autocomplete?q=${encodeURIComponent(query)}`)
        .then(response => response.json())
        .then(data => {
          if (data && data.length > 0) {
            showSuggestions(data, query);
          } else {
            hideAutocomplete();
          }
        })
        .catch(err => {
          console.error('Autocomplete error:', err);
          hideAutocomplete();
        });
    }
    
    // Hiển thị suggestions
    function showSuggestions(suggestions, query) {
      const list = createAutocompleteList();
      list.innerHTML = '';
      currentFocus = -1;
      
      suggestions.forEach((suggestion, index) => {
        const item = document.createElement('div');
        item.className = 'autocomplete-item';
        item.style.cssText = `
          padding: 12px 16px;
          cursor: pointer;
          color: #fff;
          transition: all 0.2s;
          border-bottom: 1px solid rgba(255, 255, 255, 0.05);
        `;
        
        // Highlight matched text
        const regex = new RegExp(`(${query})`, 'gi');
        const highlighted = suggestion.replace(regex, '<strong style="color: #ff6b6b;">$1</strong>');
        item.innerHTML = `<i class="bi bi-search me-2" style="opacity: 0.5;"></i>${highlighted}`;
        
        // Click handler
        item.addEventListener('click', () => {
          searchInput.value = suggestion;
          hideAutocomplete();
          searchInput.form.submit();
        });
        
        // Hover effect
        item.addEventListener('mouseenter', () => {
          item.style.background = 'rgba(255, 107, 107, 0.1)';
        });
        item.addEventListener('mouseleave', () => {
          item.style.background = 'transparent';
        });
        
        list.appendChild(item);
      });
      
      list.style.display = 'block';
    }
    
    // Ẩn autocomplete
    function hideAutocomplete() {
      if (autocompleteList) {
        autocompleteList.style.display = 'none';
      }
      currentFocus = -1;
    }
    
    // Keyboard navigation
    function setActive(items) {
      if (!items) return;
      removeActive(items);
      if (currentFocus >= items.length) currentFocus = 0;
      if (currentFocus < 0) currentFocus = items.length - 1;
      items[currentFocus].style.background = 'rgba(255, 107, 107, 0.2)';
    }
    
    function removeActive(items) {
      for (let i = 0; i < items.length; i++) {
        items[i].style.background = 'transparent';
      }
    }
    
    // Event listeners
    searchInput.addEventListener('input', function(e) {
      clearTimeout(debounceTimer);
      debounceTimer = setTimeout(() => {
        fetchSuggestions(e.target.value.trim());
      }, 300); // Debounce 300ms
    });
    
    searchInput.addEventListener('keydown', function(e) {
      if (!autocompleteList || autocompleteList.style.display === 'none') return;
      
      const items = autocompleteList.getElementsByClassName('autocomplete-item');
      
      if (e.keyCode === 40) { // DOWN
        e.preventDefault();
        currentFocus++;
        setActive(items);
      } else if (e.keyCode === 38) { // UP
        e.preventDefault();
        currentFocus--;
        setActive(items);
      } else if (e.keyCode === 13) { // ENTER
        e.preventDefault();
        if (currentFocus > -1 && items[currentFocus]) {
          items[currentFocus].click();
        }
      } else if (e.keyCode === 27) { // ESC
        hideAutocomplete();
      }
    });
    
    // Click outside to close
    document.addEventListener('click', function(e) {
      if (e.target !== searchInput && !autocompleteList?.contains(e.target)) {
        hideAutocomplete();
      }
    });
  }
  
  // Initialize when DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAutocomplete);
  } else {
    initAutocomplete();
  }
})();
