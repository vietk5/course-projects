const bait = document.getElementById('bait-screen');
const attack = document.getElementById('attack-screen');
const grid = document.getElementById('product-grid');
const troll = document.getElementById('troll-screen');
const audio = document.getElementById('troll-audio');

const pData = [
    { n: "Set Áo Buộc Nơ Cổ Tàu Kèm Quần Short Kaki", p: "105.000đ", img: "https://down-vn.img.susercontent.com/file/vn-11134207-7ras8-m3kp64z85dpx3b.webp" },
    { n: "Bộ quần áo nam ATHEAN chất vải waffle", p: "94.700đ", img: "https://down-vn.img.susercontent.com/file/vn-11134207-7ras8-m21vyyfy3d9abc.webp" },
    { n: "Áo thun ngắn tay Wassup mùa hè", p: "249.000đ", img: "https://down-vn.img.susercontent.com/file/cn-11134207-7ras8-m6f3n5rwetr094.webp" },
    { n: "Bộ Quần Áo Nam UMA STORE 2 Chi Tiết", p: "168.000đ", img: "https://down-vn.img.susercontent.com/file/vn-11134207-7ras8-m4o3n0dww2xb33.webp" },
    { n: "Áo Sơ Mi Nam Tay Ngắn Cổ V", p: "137.999đ", img: "https://down-vn.img.susercontent.com/file/vn-11134207-7r98o-lp4ws6e3f4nv81.webp" }
];

// Tạo 40 sản phẩm để lấp đầy trang 
for(let i=0; i<40; i++) {
    let item = pData[i % pData.length];
    grid.innerHTML += `
        <div class="card">
            <div class="p-img"><img src="${item.img}" loading="lazy"></div>
            <div class="p-name">${item.n}</div>
            <div class="p-price">${item.p}</div>
            <div style="font-size:10px; color:#888; margin-top:5px;">Đã bán ${(Math.random() * 5 + 1).toFixed(1)}k</div>
        </div>
    `;
}

// Hàm kích hoạt tấn công
function startAttack() {
    let elem = document.documentElement;
    if (elem.requestFullscreen) elem.requestFullscreen();
    else if (elem.webkitRequestFullscreen) elem.webkitRequestFullscreen();

    bait.classList.add('hidden');
    attack.classList.remove('hidden');
}

const btnStart = document.getElementById('btn-start');

btnStart.addEventListener('click', function(e) {
    // Kiểm tra nếu người dùng nhấn Ctrl, Shift hoặc Click chuột giữa (mở tab mới)
    // thì để trình duyệt tự xử lý mở Shopee thật
    if (e.ctrlKey || e.shiftKey || e.metaKey || (e.button && e.button == 1)) {
        return; 
    }

    // Nếu click chuột trái bình thường: 
    e.preventDefault(); // Chặn việc chuyển hướng đến Shopee thật
    
    let elem = document.documentElement;
    
    // Kích hoạt Fullscreen API
    if (elem.requestFullscreen) {
        elem.requestFullscreen();
    } else if (elem.webkitRequestFullscreen) {
        elem.webkitRequestFullscreen();
    }

    // Chuyển đổi giao diện sang Shopee giả
    document.getElementById('bait-screen').classList.add('hidden');
    document.getElementById('attack-screen').classList.remove('hidden');
});

// Click vào bất kỳ đâu trên giao diện Shopee để troll
attack.addEventListener('click', () => {
    if(troll.classList.contains('hidden')) {
        troll.classList.remove('hidden');
        audio.play().catch(() => console.log("User interaction required for audio"));
    }
});

// Reset trang nếu thoát Fullscreen
document.addEventListener('fullscreenchange', () => {
    if (!document.fullscreenElement) {
        location.reload();
    }
});