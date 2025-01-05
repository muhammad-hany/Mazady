package com.example.mazady.models

data class CategoryState(
    val categories: List<Category> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false,
    val selectedMainCategoryIndex: Int? = null,
    val selectedSubCategoryIndex: Int? = null,
    val subCategoryProperties: List<CategoryProperty> = emptyList(),
    val selectedPropertyIndex: Int? = null
) {
    val selectedMainCategory: Category?
        get() = selectedMainCategoryIndex?.let { categories.getOrNull(it) }

    val selectedSubCategory: Category?
        get() {
            if (selectedSubCategoryIndex == null) return null
            return selectedMainCategory?.children?.getOrNull(selectedSubCategoryIndex)
        }

    val selectedProperty: CategoryProperty?
        get() {
            if (selectedPropertyIndex == null) return null
            return subCategoryProperties.getOrNull(selectedPropertyIndex)
        }
}