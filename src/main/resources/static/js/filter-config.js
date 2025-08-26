// 필터 설정 관리
const FilterConfig = {
    // 필터 타입 정의
    filterTypes: {
        GENDER: 'genderFilters',
        SIZE: 'sizeFilters',
        COLOR: 'colorFilters',
        PRICE: 'priceFilters'
    },

    // 필터 파라미터 이름 매핑
    paramNames: {
        genderIds: 'genderFilters',
        sizeIds: 'sizeFilters',
        colorIds: 'colorFilters',
        minPrice: 'minPrice',
        maxPrice: 'maxPrice'
    },

    // 필터 초기값
    defaultFilters: {
        genderFilters: [],
        sizeFilters: [],
        colorFilters: [],
        minPrice: '',
        maxPrice: ''
    },

    // 필터 유효성 검사 규칙
    validation: {
        minPrice: (value) => !isNaN(value) && value >= 0,
        maxPrice: (value) => !isNaN(value) && value >= 0,
        priceRange: (min, max) => {
            if (!min || !max) return true;
            return parseInt(min) <= parseInt(max);
        }
    },

    // 필터 적용 시 URL 파라미터 처리
    buildUrlParams: (filters) => {
        const params = new URLSearchParams();
        
        Object.entries(filters).forEach(([key, value]) => {
            if (value && value.length > 0) {
                if (Array.isArray(value)) {
                    params.set(key, value.join(','));
                } else {
                    params.set(key, value);
                }
            }
        });
        
        return params;
    },

    // URL 파라미터에서 필터 상태 복원
    parseUrlParams: (urlParams) => {
        const filters = { ...FilterConfig.defaultFilters };
        
        Object.entries(FilterConfig.paramNames).forEach(([paramName, filterKey]) => {
            const value = urlParams.get(paramName);
            if (value) {
                if (paramName.includes('Ids')) {
                    filters[filterKey] = value.split(',').map(id => parseInt(id));
                } else {
                    filters[filterKey] = value;
                }
            }
        });
        
        return filters;
    }
};

// 전역에서 사용할 수 있도록 export
if (typeof module !== 'undefined' && module.exports) {
    module.exports = FilterConfig;
} else {
    window.FilterConfig = FilterConfig;
}
