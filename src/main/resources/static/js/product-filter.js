class ProductFilter {
    constructor() {
        this.form = document.getElementById('filterForm');
        this.filters = { ...FilterConfig.defaultFilters };
        
        this.initializeEventListeners();
        this.loadCurrentFilters();
    }

    initializeEventListeners() {
        // 가격 필터 적용 버튼
        const priceFilterBtn = document.getElementById('priceFilterBtn');
        if (priceFilterBtn) {
            priceFilterBtn.addEventListener('click', () => this.applyFilters());
        }

        // 전체 필터 적용 버튼
        const applyFiltersBtn = document.getElementById('applyFiltersBtn');
        if (applyFiltersBtn) {
            applyFiltersBtn.addEventListener('click', () => this.applyFilters());
        }

        // 필터 초기화 버튼
        const clearFiltersBtn = document.getElementById('clearFiltersBtn');
        if (clearFiltersBtn) {
            clearFiltersBtn.addEventListener('click', () => this.clearAllFilters());
        }

        // 체크박스 이벤트 리스너
        this.form.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
            checkbox.addEventListener('change', (e) => {
                const value = e.target.value;
                const filterType = this.getFilterType(e.target.name);
                
                if (e.target.checked) {
                    this.filters[filterType].push(parseInt(value));
                } else {
                    this.filters[filterType] = this.filters[filterType].filter(id => id !== parseInt(value));
                }
            });
        });

        // 가격 입력 필드
        const minPriceInput = document.getElementById('minPrice');
        const maxPriceInput = document.getElementById('maxPrice');
        
        if (minPriceInput) {
            minPriceInput.addEventListener('input', (e) => {
                this.filters.minPrice = e.target.value;
            });
        }
        
        if (maxPriceInput) {
            maxPriceInput.addEventListener('input', (e) => {
                this.filters.maxPrice = e.target.value;
            });
        }

        // 폼 제출 이벤트 가로채기 (중복 파라미터 방지)
        this.form.addEventListener('submit', (e) => {
            this.cleanupFormBeforeSubmit();
        });
    }

    getFilterType(name) {
        const paramNames = FilterConfig.paramNames;
        for (const [paramName, filterKey] of Object.entries(paramNames)) {
            if (name.includes(paramName.replace('Ids', ''))) {
                return filterKey;
            }
        }
        return 'unknown';
    }

    loadCurrentFilters() {
        // URL 파라미터에서 현재 필터 상태 로드
        const urlParams = new URLSearchParams(window.location.search);
        this.filters = FilterConfig.parseUrlParams(urlParams);
        
        // UI 상태 복원
        this.restoreUIState();
    }

    restoreUIState() {
        // 체크박스 상태 복원
        Object.entries(FilterConfig.paramNames).forEach(([paramName, filterKey]) => {
            if (paramName.includes('Ids') && this.filters[filterKey]) {
                this.filters[filterKey].forEach(id => {
                    const checkbox = this.form.querySelector(`input[name="${paramName}"][value="${id}"]`);
                    if (checkbox) checkbox.checked = true;
                });
            }
        });

        // 가격 입력 필드 복원
        if (this.filters.minPrice) {
            const minPriceInput = document.getElementById('minPrice');
            if (minPriceInput) minPriceInput.value = this.filters.minPrice;
        }
        if (this.filters.maxPrice) {
            const maxPriceInput = document.getElementById('maxPrice');
            if (maxPriceInput) maxPriceInput.value = this.filters.maxPrice;
        }
    }

    applyFilters() {
        // 기존 hidden input 제거
        this.removeHiddenInputs();

        // 필터 값들을 hidden input으로 추가
        this.addFilterInputs();

        // 폼 제출 (이벤트 리스너가 자동으로 cleanupFormBeforeSubmit 호출)
        this.form.submit();
    }

    addFilterInputs() {
        // 성별 필터 - 체크된 것만 추가
        const checkedGenderIds = this.getCheckedValues('genderIds');
        if (checkedGenderIds.length > 0) {
            this.addHiddenInput('genderIds', checkedGenderIds.join(','));
            console.log('Added genderIds:', checkedGenderIds);
        }

        // 사이즈 필터 - 체크된 것만 추가
        const checkedSizeIds = this.getCheckedValues('sizeIds');
        if (checkedSizeIds.length > 0) {
            this.addHiddenInput('sizeIds', checkedSizeIds.join(','));
            console.log('Added sizeIds:', checkedSizeIds);
        }

        // 색상 필터 - 체크된 것만 추가
        const checkedColorIds = this.getCheckedValues('colorIds');
        if (checkedColorIds.length > 0) {
            this.addHiddenInput('colorIds', checkedColorIds.join(','));
            console.log('Added colorIds:', checkedColorIds);
        }

        // 가격 필터 - 값이 있을 때만 추가
        if (this.filters.minPrice && this.filters.minPrice.trim() !== '') {
            this.addHiddenInput('minPrice', this.filters.minPrice);
            console.log('Added minPrice:', this.filters.minPrice);
        }
        if (this.filters.maxPrice && this.filters.maxPrice.trim() !== '') {
            this.addHiddenInput('maxPrice', this.filters.maxPrice);
            console.log('Added maxPrice:', this.filters.maxPrice);
        }
    }

    getCheckedValues(inputName) {
        const checkedInputs = this.form.querySelectorAll(`input[name="${inputName}"]:checked`);
        const values = Array.from(checkedInputs)
            .map(input => input.value)
            .filter(value => value && value.trim() !== ''); // 빈 값 필터링
        
        console.log(`Checked ${inputName}:`, values);
        return values;
    }

    clearAllFilters() {
        // 필터 상태 초기화
        this.filters = { ...FilterConfig.defaultFilters };

        // 모든 체크박스 해제
        this.form.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
            checkbox.checked = false;
        });

        // 가격 입력 필드 초기화
        const minPriceInput = document.getElementById('minPrice');
        const maxPriceInput = document.getElementById('maxPrice');
        if (minPriceInput) minPriceInput.value = '';
        if (maxPriceInput) maxPriceInput.value = '';

        // 기존 hidden input 제거
        this.removeHiddenInputs();

        // 폼 제출
        this.form.submit();
    }

    removeHiddenInputs() {
        const inputsToRemove = ['genderIds', 'sizeIds', 'colorIds', 'minPrice', 'maxPrice'];
        inputsToRemove.forEach(name => {
            // 같은 이름의 모든 input을 찾아서 제거
            const inputs = this.form.querySelectorAll(`input[name="${name}"]`);
            inputs.forEach(input => {
                if (input.type === 'hidden') {
                    input.remove();
                }
            });
        });
    }

    addHiddenInput(name, value) {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = name;
        input.value = value;
        this.form.appendChild(input);
    }

    cleanupFormBeforeSubmit() {
        // 필터 관련 hidden input만 정리 (기존 검색 파라미터는 유지)
        const filterInputs = ['genderIds', 'sizeIds', 'colorIds', 'minPrice', 'maxPrice'];
        filterInputs.forEach(name => {
            const inputs = this.form.querySelectorAll(`input[name="${name}"]`);
            inputs.forEach(input => {
                if (input.type === 'hidden') {
                    input.remove();
                }
            });
        });

        // 체크된 옵션만 다시 추가 (최종 확인)
        this.addFilterInputs();
    }
}

// 페이지 로드 시 필터 초기화
document.addEventListener('DOMContentLoaded', () => {
    new ProductFilter();
});
