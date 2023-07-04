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

import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.ProductConstants.OtherCase;
import com.happidreampets.app.constants.ProductConstants.SpecialCharacter;
import com.happidreampets.app.controller.APIController.ERROR_CODES;
import com.happidreampets.app.constants.AnimalConstants.ExceptionMessageCase;
import com.happidreampets.app.constants.AnimalConstants.LowerCase;
import com.happidreampets.app.database.model.Animal;
import com.happidreampets.app.database.model.Animal.ANIMALCOLUMN;
import com.happidreampets.app.database.repository.AnimalRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;

@Component
public class AnimalCRUD {

    @Autowired
    private AnimalRepository animalRepository;

    private DbFilter dbFilter;

    public static final String ANIMALS_IMAGE_LOCATION_FULL = System
            .getProperty(ProductConstants.OtherCase.USER_DOT_DIR)
            + "/src/main/resources" + ProductConstants.SpecialCharacter.SLASH + ProductConstants.LowerCase.STATIC
            + ProductConstants.SpecialCharacter.SLASH
            + LowerCase.ANIMALS;

    public static final String ANIMALS_IMAGE_LOCATION_BEFORE_STATIC = System.getProperty(OtherCase.USER_DOT_DIR)
            + "/src/main/resources";

    public static final String ANIMALS_IMAGE_LOCATION_FROM_STATIC = ProductConstants.LowerCase.STATIC
            + ProductConstants.SpecialCharacter.SLASH
            + LowerCase.ANIMALS;

    public DbFilter getDbFilter() {
        return dbFilter;
    }

    public void setDbFilter(DbFilter dbFilter) {
        this.dbFilter = dbFilter;
    }

    private ANIMALCOLUMN checkAndGetColumnName() {
        if (dbFilter != null) {
            if (EnumUtils.isValidEnum(ANIMALCOLUMN.class, dbFilter.getSortColumn().toString())) {
                ANIMALCOLUMN enumValue = ANIMALCOLUMN.valueOf(dbFilter.getSortColumn().toString());
                return enumValue;
            }
        }
        return null;
    }

    private JSONObject getDataInRequiredFormat(Iterable<Animal> data) {
        JSONObject responseData = new JSONObject();
        responseData.put(ProductConstants.LowerCase.DATA, JSONObject.NULL);
        if (getDbFilter() != null) {
            if (getDbFilter().getFormat().equals(DATAFORMAT.JSON)) {
                JSONArray responseArray = new JSONArray();
                data.forEach(row -> {
                    responseArray.put(row.toJSON());
                });
                responseData.put(ProductConstants.LowerCase.DATA, responseArray);
            } else if (getDbFilter().getFormat().equals(DATAFORMAT.POJO)) {
                List<Animal> responseList = new ArrayList<>();
                data.forEach(responseList::add);
                responseData.put(ProductConstants.LowerCase.DATA, responseList);
            }
        } else {
            List<Animal> responseList = new ArrayList<>();
            data.forEach(responseList::add);
            responseData.put(ProductConstants.LowerCase.DATA, responseList);
        }

        return responseData;
    }

    private JSONObject getPageData(Page<Animal> animalsPage) {
        JSONObject pageData = new JSONObject();
        pageData.put(ProductConstants.LowerCase.PAGE, animalsPage.getNumber() + 1);
        pageData.put(ProductConstants.SnakeCase.PER_PAGE, animalsPage.getSize());
        pageData.put(ProductConstants.LowerCase.COUNT, animalsPage.getContent().size());
        pageData.put(ProductConstants.SnakeCase.MORE_RECORDS, animalsPage.hasNext());
        return pageData;
    }

    public JSONObject getAnimals() {
        JSONObject animals = new JSONObject();
        Sort sort = null;
        if (getDbFilter() != null && checkAndGetColumnName() != null) {
            sort = Sort.by(getDbFilter().getSortDirection(), checkAndGetColumnName().getColumnName());
        }
        Integer startIndex = getDbFilter() != null ? getDbFilter().getStartIndex() : 0;
        Integer limit = getDbFilter() != null ? getDbFilter().getLimitIndex() : 0;
        Pageable pageable = sort != null ? PageRequest.of(startIndex, limit, sort) : PageRequest.of(startIndex, limit);
        Page<Animal> animalsPage = animalRepository.findAllByToBeDeletedIsFalse(pageable);
        Iterable<Animal> animalsIterable = animalsPage.getContent();
        animals.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(animalsIterable).get(ProductConstants.LowerCase.DATA));
        animals.put(ProductConstants.LowerCase.INFO, getPageData(animalsPage));
        return animals;
    }

    public JSONObject getAnimalInJSON(Long id) {
        Animal animal = animalRepository.findByIdAndToBeDeletedIsFalse(id);
        if (animal == null) {
            return null;
        } else {
            return animal.toJSON();
        }
    }

    public Animal getAnimal(Long id) {
        return animalRepository.findByIdAndToBeDeletedIsFalse(id);
    }

    public Animal createAnimal(String name, String description) {
        Animal animal = new Animal();
        animal.setName(name);
        animal.setDescription(description);
        animal.setAddedTime(System.currentTimeMillis());
        return animalRepository.save(animal);
    }

    public Animal updateAnimal(Long id, String name, String description, String image) throws Exception {
        Animal animal = animalRepository.findByIdAndToBeDeletedIsFalse(id);
        if (null == animal) {
            throw new Exception(ExceptionMessageCase.ANIMAL_NOT_FOUND);
        }
        if (name != null) {
            animal.setName(name);
        }
        if (description != null) {
            animal.setDescription(description);
        }
        if (image != null) {
            animal.setImage(image);
        }
        return animalRepository.save(animal);
    }

    public Boolean deleteAnimalById(Long id) throws Exception {
        Animal animal = animalRepository.findByIdAndToBeDeletedIsFalse(id);
        if (null == animal) {
            throw new Exception(ExceptionMessageCase.ANIMAL_NOT_FOUND);
        }
        animal.setToBeDeleted(Boolean.TRUE);
        animal.setToBeDeletedStatusChangeTime(System.currentTimeMillis());
        animalRepository.save(animal);
        return true;
    }

    public Boolean deleteAnimalByName(String name) throws Exception {
        List<Animal> animalList = animalRepository.findByNameAndToBeDeletedIsFalse(name);
        if (animalList.isEmpty()) {
            throw new Exception(ExceptionMessageCase.ANIMAL_NOT_FOUND);
        }
        for (Animal animal : animalList) {
            animal.setToBeDeleted(Boolean.TRUE);
            animal.setToBeDeletedStatusChangeTime(System.currentTimeMillis());
            animalRepository.save(animal);
        }
        return true;
    }

    public JSONObject checkBodyOfCreateAnimal(JSONObject body) {
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

    public void addImageToAnimal(Animal animal, InputStream image, String extension) throws Exception {
        String ANIMAL_IMAGE_FOLDER = ANIMALS_IMAGE_LOCATION_FULL;
        String existingImage = animal.getImage();
        if (existingImage != null) {
            throw new Exception(ExceptionMessageCase.MAXIMUM_IMAGES_FOR_ANIMAL_REACHED);
        }
        Path path = Paths.get(
                ANIMAL_IMAGE_FOLDER + ProductConstants.SpecialCharacter.SLASH
                        + getFileName(animal.getId(), extension));
        addImageToServer(image, path);
        existingImage = ANIMALS_IMAGE_LOCATION_FROM_STATIC + ProductConstants.SpecialCharacter.SLASH
                + getFileName(animal.getId(), extension);
        animal.setImage(existingImage);
        animalRepository.save(animal);
    }

    public void deleteImageFromAnimal(Animal animal) throws Exception {
        String ANIMAL_IMAGE_FOLDER = ANIMALS_IMAGE_LOCATION_BEFORE_STATIC;
        String animalImage = animal.getImage();
        if (animalImage == null) {
            throw new Exception(ExceptionMessageCase.NO_IMAGE_PRESENT_FOR_ANIMAL);
        }
        Path path = Paths.get(
                ANIMAL_IMAGE_FOLDER + SpecialCharacter.SLASH + animalImage);
        deleteImageFromServer(path);
        animal.setImage(null);
        animalRepository.save(animal);
    }

    public void addImageToServer(InputStream image, Path path) throws IOException {
        Files.copy(image, path);
    }

    public void deleteImageFromServer(Path path) throws IOException {
        Files.delete(path);
    }

    public String getFileName(Long animalId, String extension) {
        return animalId + ProductConstants.SpecialCharacter.DOT + extension;
    }
}
