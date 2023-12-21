package com.happidreampets.app.database.crud;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.happidreampets.app.constants.AnimalConstants;
import com.happidreampets.app.constants.CategoryConstants;
import com.happidreampets.app.constants.CategoryConstants.LowerCase;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.ProductConstants.OtherCase;
import com.happidreampets.app.constants.ProductConstants.SpecialCharacter;
import com.happidreampets.app.controller.APIController.ERROR_CODES;
import com.happidreampets.app.constants.CategoryConstants.ExceptionMessageCase;
import com.happidreampets.app.database.model.Animal;
import com.happidreampets.app.database.model.AnimalOrCategoryImage;
import com.happidreampets.app.database.model.Category;
import com.happidreampets.app.database.model.Category.CATEGORYCOLUMN;
import com.happidreampets.app.database.repository.CategoryRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;

@Component
public class CategoryCRUD {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AnimalCRUD animalCRUD;

    private DbFilter dbFilter;

    public static final String CATEGORY_IMAGE_LOCATION_FULL = System
            .getProperty(ProductConstants.OtherCase.USER_DOT_DIR)
            + "/src/main/resources" + ProductConstants.SpecialCharacter.SLASH + ProductConstants.LowerCase.STATIC
            + ProductConstants.SpecialCharacter.SLASH
            + LowerCase.CATEGORY;

    public static final String CATEGORY_IMAGE_LOCATION_BEFORE_STATIC = System.getProperty(OtherCase.USER_DOT_DIR)
            + "/src/main/resources";

    public static final String CATEGORY_IMAGE_LOCATION_FROM_STATIC = ProductConstants.LowerCase.STATIC
            + ProductConstants.SpecialCharacter.SLASH
            + LowerCase.CATEGORY;

    public DbFilter getDbFilter() {
        return dbFilter;
    }

    public void setDbFilter(DbFilter dbFilter) {
        this.dbFilter = dbFilter;
    }

    private CATEGORYCOLUMN checkAndGetColumnName() {
        if (dbFilter != null) {
            if (EnumUtils.isValidEnum(CATEGORYCOLUMN.class, dbFilter.getSortColumn().toString())) {
                CATEGORYCOLUMN enumValue = CATEGORYCOLUMN.valueOf(dbFilter.getSortColumn().toString());
                return enumValue;
            }
        }
        return null;
    }

    private JSONObject getDataInRequiredFormat(Iterable<Category> data) {
        JSONObject responseData = new JSONObject();
        responseData.put(ProductConstants.LowerCase.DATA, JSONObject.NULL);
        if (getDbFilter() != null) {
            if (getDbFilter().getFormat().equals(DATAFORMAT.JSON)) {
                JSONArray responseArray = new JSONArray();
                data.forEach(row -> {
                    responseArray.put(row.toJSON(Boolean.TRUE, Boolean.TRUE));
                });
                responseData.put(ProductConstants.LowerCase.DATA, responseArray);
            } else if (getDbFilter().getFormat().equals(DATAFORMAT.POJO)) {
                List<Category> responseList = new ArrayList<>();
                data.forEach(responseList::add);
                responseData.put(ProductConstants.LowerCase.DATA, responseList);
            }
        } else {
            List<Category> responseList = new ArrayList<>();
            data.forEach(responseList::add);
            responseData.put(ProductConstants.LowerCase.DATA, responseList);
        }

        return responseData;
    }

    private JSONObject getPageData(Page<Category> categoryPage) {
        JSONObject pageData = new JSONObject();
        pageData.put(ProductConstants.LowerCase.PAGE, categoryPage.getNumber() + 1);
        pageData.put(ProductConstants.SnakeCase.PER_PAGE, categoryPage.getSize());
        pageData.put(ProductConstants.LowerCase.COUNT, categoryPage.getContent().size());
        pageData.put(ProductConstants.SnakeCase.MORE_RECORDS, categoryPage.hasNext());
        pageData.put(ProductConstants.SnakeCase.TOTAL_RECORDS, categoryPage.getTotalElements());
        return pageData;
    }

    public JSONObject getAllCategories(Animal animal) {
        JSONObject categories = new JSONObject();
        Sort sort = null;
        if (getDbFilter() != null && checkAndGetColumnName() != null) {
            sort = Sort.by(getDbFilter().getSortDirection(), checkAndGetColumnName().getColumnName());
        }
        List<Category> categoryData = sort != null
                ? categoryRepository.findAllByAnimalAndToBeDeletedIsFalse(animal, sort)
                : categoryRepository.findAllByAnimalAndToBeDeletedIsFalse(animal);
        categories.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(categoryData).get(ProductConstants.LowerCase.DATA));
        return categories;
    }

    public JSONObject getCategoryDetails(Animal animal) {
        JSONObject categoryData = new JSONObject();
        Sort sort = null;
        if (getDbFilter() != null && checkAndGetColumnName() != null) {
            sort = Sort.by(getDbFilter().getSortDirection(), checkAndGetColumnName().getColumnName());
        }
        Integer startIndex = getDbFilter() != null ? getDbFilter().getStartIndex() : 0;
        Integer limit = getDbFilter() != null ? getDbFilter().getLimitIndex() : 0;
        Pageable pageable = sort != null ? PageRequest.of(startIndex, limit, sort) : PageRequest.of(startIndex, limit);
        Page<Category> categoryPage = categoryRepository.findAllByAnimalAndToBeDeletedIsFalse(pageable, animal);
        Iterable<Category> categoryIterable = categoryPage.getContent();
        categoryData.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(categoryIterable).get(ProductConstants.LowerCase.DATA));
        categoryData.put(ProductConstants.LowerCase.INFO, getPageData(categoryPage));
        return categoryData;
    }

    public Category getCategoryDetail(Long animalId, Long categoryId) {
        return categoryRepository.findByAnimalAndIdAndToBeDeletedIsFalse(animalCRUD.getAnimal(animalId), categoryId);
    }

    public Category getCategoryDetail(Animal animal, Long id) {
        return categoryRepository.findByAnimalAndIdAndToBeDeletedIsFalse(animal, id);
    }

    public Category getCategoryDetailBasedOnId(Long id) {
        return categoryRepository.findByIdAndToBeDeletedIsFalse(id);
    }

    public Category createCategory(String name, String description, Animal animal) throws Exception {
        Category category = new Category();
        if (name == null) {
            throw new Exception(ProductConstants.ExceptionMessageCase.NAME_CANNOT_BE_EMPTY);
        }
        if (animal == null) {
            throw new Exception(ExceptionMessageCase.ANIMAL_ASSOCIATION_INVALID);
        }
        category.setName(name);
        category.setDescription(description);
        category.setAnimal(animal);
        category.setAddedTime(System.currentTimeMillis());
        return categoryRepository.save(category);
    }

    public Category updateCategoryByName(Long id, String name) throws Exception {
        Category category = categoryRepository.findByIdAndToBeDeletedIsFalse(id);
        if (null == category) {
            throw new Exception(ExceptionMessageCase.CATEGORY_NOT_FOUND);
        }
        if (name != null) {
            category.setName(name);
        }
        return categoryRepository.save(category);
    }

    public Category updateCategoryAnimal(Long id, Animal animal) throws Exception {
        Category category = categoryRepository.findByIdAndToBeDeletedIsFalse(id);
        if (null == category) {
            throw new Exception(ExceptionMessageCase.CATEGORY_NOT_FOUND);
        }
        if (animal != null) {
            category.setAnimal(animal);
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, String name, String description, Animal animal)
            throws Exception {
        Category category = categoryRepository.findByIdAndToBeDeletedIsFalse(id);
        if (null == category) {
            throw new Exception(ExceptionMessageCase.CATEGORY_NOT_FOUND);
        }
        if (name != null) {
            category.setName(name);
        }
        category.setDescription(description);
        if (animal != null) {
            category.setAnimal(animal);
        }
        return categoryRepository.save(category);
    }

    public Boolean deleteCategoryById(Long id) throws Exception {
        Category category = categoryRepository.findByIdAndToBeDeletedIsFalse(id);
        if (null == category) {
            throw new Exception(ExceptionMessageCase.CATEGORY_NOT_FOUND);
        }
        category.setToBeDeleted(Boolean.TRUE);
        category.setToBeDeletedStatusChangeTime(System.currentTimeMillis());
        categoryRepository.save(category);
        return true;
    }

    public Boolean deleteCategoryByName(String name) throws Exception {
        List<Category> categoryList = categoryRepository.findByNameAndToBeDeletedIsFalse(name);
        if (categoryList.isEmpty()) {
            throw new Exception(AnimalConstants.ExceptionMessageCase.ANIMAL_NOT_FOUND);
        }
        for (Category category : categoryList) {
            category.setToBeDeleted(Boolean.TRUE);
            category.setToBeDeletedStatusChangeTime(System.currentTimeMillis());
            categoryRepository.save(category);
        }
        return true;
    }

    public JSONObject checkBodyOfCreateCategory(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(ProductConstants.LowerCase.NAME)) {
            missingField = ProductConstants.LowerCase.NAME;
            message = message.replace(ProductConstants.LoggerCase.ARG0, ProductConstants.LowerCase.NAME);
        } else if (!body.has(ProductConstants.LowerCase.DESCRIPTION)) {
            missingField = ProductConstants.LowerCase.DESCRIPTION;
            message = message.replace(ProductConstants.LoggerCase.ARG0, ProductConstants.LowerCase.DESCRIPTION);
        } else {
            isSuccess = Boolean.TRUE;
        }
        response.put(ProductConstants.LowerCase.SUCCESS, isSuccess);
        if (!isSuccess) {
            response.put(ProductConstants.LowerCase.DATA,
                    new JSONObject().put(ProductConstants.LowerCase.FIELD, missingField)
                            .put(ProductConstants.LowerCase.CODE, code)
                            .put(ProductConstants.LowerCase.MESSAGE, message));
        }
        return response;
    }

    public Boolean isValidFileExtension(String fileFullName) {
        String extension = fileFullName.substring(fileFullName.lastIndexOf(ProductConstants.SpecialCharacter.DOT) + 1);
        List<String> allowedExtension = new ArrayList<>();
        allowedExtension.add(ProductConstants.LowerCase.JPG);
        allowedExtension.add(ProductConstants.LowerCase.JPEG);
        allowedExtension.add(ProductConstants.LowerCase.PNG);
        return allowedExtension.contains(extension);
    }

    public String getExtension(String fileFullName) {
        return fileFullName.substring(fileFullName.lastIndexOf(ProductConstants.SpecialCharacter.DOT) + 1);
    }

    public void addImageUrlToCategory(Category category, String imageUrl) throws Exception {
        AnimalOrCategoryImage existingImage = category.getImage();
        if (existingImage != null) {
            throw new Exception(ExceptionMessageCase.MAXIMUM_IMAGES_FOR_CATEGORY_REACHED);
        }
        existingImage = new AnimalOrCategoryImage();
        existingImage.setImageUrl(imageUrl);
        existingImage.setImageType("external_url");
        category.setImage(existingImage);
        categoryRepository.save(category);
    }

    public void addImageToCategory(Category category, InputStream image, String extension) throws Exception {
        String CATEGORY_IMAGE_FOLDER = CATEGORY_IMAGE_LOCATION_FULL;
        AnimalOrCategoryImage existingImage = category.getImage();
        if (existingImage != null) {
            throw new Exception(ExceptionMessageCase.MAXIMUM_IMAGES_FOR_CATEGORY_REACHED);
        }
        Path path = Paths.get(
                CATEGORY_IMAGE_FOLDER + ProductConstants.SpecialCharacter.SLASH
                        + getFileName(category, extension));
        addImageToServer(image, path);
        existingImage = new AnimalOrCategoryImage();
        existingImage.setImageUrl(CATEGORY_IMAGE_LOCATION_FROM_STATIC + ProductConstants.SpecialCharacter.SLASH
                + getFileName(category, extension));
        existingImage.setImageType("file");
        category.setImage(existingImage);
        categoryRepository.save(category);
    }

    public void deleteImageFromCategory(Category category) throws Exception {
        String CATEGORY_IMAGE_FOLDER = CATEGORY_IMAGE_LOCATION_BEFORE_STATIC;
        AnimalOrCategoryImage categoryImage = category.getImage();
        if (categoryImage == null) {
            throw new Exception(CategoryConstants.ExceptionMessageCase.NO_IMAGE_PRESENT_FOR_CATEGORY);
        }
        if (categoryImage.getImageType().equals("file")) {
            Path path = Paths.get(
                    CATEGORY_IMAGE_FOLDER + SpecialCharacter.SLASH + categoryImage);
            deleteImageFromServer(path);
        }
        category.setImage(null);
        categoryRepository.save(category);
    }

    public void addImageToServer(InputStream image, Path path) throws IOException {
        Files.copy(image, path);
    }

    public void deleteImageFromServer(Path path) throws IOException {
        Files.delete(path);
    }

    public String getFileName(Category category, String extension) {
        return category.getAnimal().getId() + ProductConstants.SpecialCharacter.DOT + category.getId()
                + ProductConstants.SpecialCharacter.DOT
                + extension;
    }

}
