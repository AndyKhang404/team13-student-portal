/* Student Portal — micro-interactions (progressive enhancement, no dependency) */
(function () {
  // 1. Navbar shadow on scroll
  const nav = document.querySelector('.navbar-portal');
  const onScroll = () => { if (nav) nav.classList.toggle('scrolled', window.scrollY > 8); };
  window.addEventListener('scroll', onScroll, { passive: true });
  onScroll();

  // 2. Reveal on scroll (IntersectionObserver, stagger via --d)
  const io = new IntersectionObserver((entries) => {
    entries.forEach((e) => {
      if (e.isIntersecting) {
        e.target.classList.add('in');
        io.unobserve(e.target);
      }
    });
  }, { threshold: 0.12, rootMargin: '0px 0px -40px 0px' });
  document.querySelectorAll('.reveal').forEach((el) => io.observe(el));

  // 3. Count-up numbers [data-count-to]
  const counters = document.querySelectorAll('[data-count-to]');
  const cio = new IntersectionObserver((entries) => {
    entries.forEach((e) => {
      if (!e.isIntersecting) return;
      const el = e.target;
      cio.unobserve(el);
      const target = parseInt(el.getAttribute('data-count-to') || '0', 10);
      const dur = 900;
      const t0 = performance.now();
      const tick = (t) => {
        const p = Math.min(1, (t - t0) / dur);
        const eased = 1 - Math.pow(1 - p, 3);
        el.textContent = Math.round(target * eased);
        if (p < 1) requestAnimationFrame(tick);
      };
      requestAnimationFrame(tick);
    });
  }, { threshold: 0.4 });
  counters.forEach((c) => cio.observe(c));

  // 4. Client-side search filter for students table
  const search = document.getElementById('studentSearch');
  const clearBtn = document.getElementById('studentSearchClear');
  const rows = () => Array.from(document.querySelectorAll('#studentsTableBody tr[data-search]'));
  const emptyRow = document.getElementById('noSearchResult');
  const countBadge = document.getElementById('visibleCount');
  function applyFilter() {
    if (!search) return;
    const q = search.value.trim().toLowerCase();
    let visible = 0;
    rows().forEach((tr) => {
      const hay = (tr.getAttribute('data-search') || '').toLowerCase();
      const show = !q || hay.includes(q);
      tr.style.display = show ? '' : 'none';
      if (show) visible++;
    });
    if (emptyRow) emptyRow.style.display = visible === 0 ? '' : 'none';
    if (countBadge) countBadge.textContent = visible;
    if (clearBtn) clearBtn.style.display = q ? '' : 'none';
  }
  if (search) {
    search.addEventListener('input', applyFilter);
    if (clearBtn) clearBtn.addEventListener('click', () => { search.value = ''; applyFilter(); search.focus(); });
  }

  // 5. Shake invalid modal on load (when server re-opens with errors)
  const modalEl = document.getElementById('addStudentModal');
  if (modalEl && modalEl.querySelector('.is-invalid')) {
    modalEl.querySelector('.modal-content')?.classList.add('shake');
    setTimeout(() => modalEl.querySelector('.modal-content')?.classList.remove('shake'), 500);
  }

  // 6. Auto-dismiss success alert after 6s with fade
  const alertEl = document.querySelector('.alert-slide');
  if (alertEl) {
    setTimeout(() => {
      alertEl.classList.remove('show');
      setTimeout(() => alertEl.remove(), 400);
    }, 6000);
  }

  // 7. Subtle tilt on feature cards (desktop, pointer fine)
  if (window.matchMedia('(pointer: fine)').matches) {
    document.querySelectorAll('[data-tilt]').forEach((card) => {
      card.addEventListener('mousemove', (ev) => {
        const r = card.getBoundingClientRect();
        const x = (ev.clientX - r.left) / r.width - 0.5;
        const y = (ev.clientY - r.top) / r.height - 0.5;
        card.style.transform = `translateY(-8px) perspective(800px) rotateX(${-y * 6}deg) rotateY(${x * 8}deg)`;
      });
      card.addEventListener('mouseleave', () => { card.style.transform = ''; });
    });
  }
})();
