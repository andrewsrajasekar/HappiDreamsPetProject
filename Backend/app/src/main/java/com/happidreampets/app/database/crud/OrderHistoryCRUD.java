package com.happidreampets.app.database.crud;

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

import com.happidreampets.app.constants.ControllerConstants;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.ProductConstants.LowerCase;
import com.happidreampets.app.constants.ProductConstants.MessageCase;
import com.happidreampets.app.constants.UserAddressConstants;
import com.happidreampets.app.controller.APIController.ERROR_CODES;
import com.happidreampets.app.constants.OrderHistoryConstants.ExceptionMessageCase;
import com.happidreampets.app.constants.OrderHistoryConstants.SnakeCase;
import com.happidreampets.app.database.model.OrderHistory;
import com.happidreampets.app.database.model.OrderHistory.ORDERHISTORYCOLUMN;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.model.User;
import com.happidreampets.app.database.model.UserAddress;
import com.happidreampets.app.database.repository.OrderHistoryRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;

@Component
public class OrderHistoryCRUD {

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private ProductCRUD productCRUD;

    @Autowired
    private UserAddressCRUD userAddressCRUD;

    private Boolean fromController = Boolean.FALSE;

    public Boolean getFromController() {
        return fromController;
    }

    public void setFromController(Boolean fromController) {
        this.fromController = fromController;
    }

    private DbFilter dbFilter;

    public DbFilter getDbFilter() {
        return dbFilter;
    }

    public void setDbFilter(DbFilter dbFilter) {
        this.dbFilter = dbFilter;
    }

    private ORDERHISTORYCOLUMN checkAndGetColumnName() {
        if (dbFilter != null) {
            if (EnumUtils.isValidEnum(ORDERHISTORYCOLUMN.class, dbFilter.getSortColumn().toString())) {
                ORDERHISTORYCOLUMN enumValue = ORDERHISTORYCOLUMN.valueOf(dbFilter.getSortColumn().toString());
                return enumValue;
            }
        }
        return null;
    }

    private JSONObject getDataInRequiredFormat(Iterable<OrderHistory> data) {
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
                List<OrderHistory> responseList = new ArrayList<>();
                data.forEach(responseList::add);
                responseData.put(ProductConstants.LowerCase.DATA, responseList);
            }
        } else {
            List<OrderHistory> responseList = new ArrayList<>();
            data.forEach(responseList::add);
            responseData.put(ProductConstants.LowerCase.DATA, responseList);
        }

        return responseData;
    }

    private JSONObject getPageData(Page<OrderHistory> orderHistoryPage) {
        JSONObject pageData = new JSONObject();
        pageData.put(ProductConstants.LowerCase.PAGE, orderHistoryPage.getNumber() + 1);
        pageData.put(ProductConstants.SnakeCase.PER_PAGE, orderHistoryPage.getSize());
        pageData.put(ProductConstants.LowerCase.COUNT, orderHistoryPage.getContent().size());
        pageData.put(ProductConstants.SnakeCase.MORE_RECORDS, orderHistoryPage.hasNext());
        return pageData;
    }

    public JSONObject getOrderHistoryDetails(User user) {
        JSONObject orderHistoryData = new JSONObject();
        Sort sort = null;
        if (getDbFilter() != null && checkAndGetColumnName() != null) {
            sort = Sort.by(getDbFilter().getSortDirection(), checkAndGetColumnName().getColumnName());
        }
        Integer startIndex = getDbFilter() != null ? getDbFilter().getStartIndex() : 0;
        Integer limit = getDbFilter() != null ? getDbFilter().getLimitIndex() : 0;
        Pageable pageable = sort != null ? PageRequest.of(startIndex, limit, sort) : PageRequest.of(startIndex, limit);
        Page<OrderHistory> orderHistoryPage = orderHistoryRepository.findAllByUser(pageable, user);
        Iterable<OrderHistory> orderHistoryIterable = orderHistoryPage.getContent();
        orderHistoryData.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(orderHistoryIterable).get(ProductConstants.LowerCase.DATA));
        orderHistoryData.put(ProductConstants.LowerCase.INFO, getPageData(orderHistoryPage));
        return orderHistoryData;
    }

    public OrderHistory bulkCreateOrderHistory(User user, List<Long> productIds, UserAddress userAddress)
            throws Exception {
        Long addedTime = System.currentTimeMillis();
        List<Product> products = new ArrayList<>();
        for (Long productId : productIds) {
            Product product = productCRUD.getProduct(productId);
            if (product == null) {
                throw new Exception(getFromController()
                        ? ProductConstants.ExceptionMessageCase.INVALID_PRODUCT_ID_HYPHEN_ARG0
                                .replace(ProductConstants.LoggerCase.ARG0, productId.toString())
                                + ControllerConstants.SpecialCharacter.UNDERSCORE
                                + ControllerConstants.CapitalizationCase.BYPASS_EXCEPTION
                        : ProductConstants.ExceptionMessageCase.INVALID_PRODUCT_ID_HYPHEN_ARG0
                                .replace(ProductConstants.LoggerCase.ARG0, productId.toString()));
            }
            products.add(product);
        }
        if (products.size() != productIds.size()) {
            throw new Exception();
        }
        return createOrderHistory(user, products, addedTime, userAddress);
    }

    public OrderHistory createOrderHistory(User user, List<Product> products, Long addedTime,
            UserAddress userAddress) {
        if (addedTime == null) {
            addedTime = System.currentTimeMillis();
        }
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setUser(user);
        orderHistory.setProducts(products.toArray(new Product[0]));
        orderHistory.setAddedTime(addedTime);
        orderHistory.setAddress(userAddress);

        OrderHistory savedOrderHistory = orderHistoryRepository
                .save(orderHistory);

        return savedOrderHistory;
    }

    public Boolean deleteOrderHistory(Long id) throws Exception {
        OrderHistory orderHistory = orderHistoryRepository.findById(id).orElse(null);
        if (orderHistory == null) {
            throw new Exception(ExceptionMessageCase.ORDER_HISTORY_NOT_FOUND_FOR_GIVEN_ID);
        }
        orderHistoryRepository.delete(orderHistory);
        return true;
    }

    public JSONObject validateBodyDataForCreate(JSONObject body, User user) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(SnakeCase.ORDER_IDS)) {
            missingField = SnakeCase.ORDER_IDS;
            message = message.replace(ProductConstants.LoggerCase.ARG0, SnakeCase.ORDER_IDS);
        } else if (!body.has(UserAddressConstants.SnakeCase.ADDRESS_ID)) {
            missingField = UserAddressConstants.SnakeCase.ADDRESS_ID;
            message = message.replace(ProductConstants.LoggerCase.ARG0, UserAddressConstants.SnakeCase.ADDRESS_ID);
        } else {
            Boolean isError = Boolean.FALSE;
            if (body.get(SnakeCase.ORDER_IDS) instanceof List) {
                List<?> productList = (List<?>) body.get(SnakeCase.ORDER_IDS);
                int index = 1;
                for (Object item : productList) {
                    if (!(item instanceof Long)) {
                        code = null;
                        message = ProductConstants.MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                        message += " in the Position " + index + " of the products array";
                        missingField = ProductConstants.LowerCase.PRODUCTS;
                        isError = Boolean.TRUE;
                        break;
                    }
                    index++;
                }
            } else {
                code = null;
                message = ProductConstants.MessageCase.THE_VALUE_SHOULD_BE_AN_ARRAY_OF_INTEGERS;
                missingField = ProductConstants.LowerCase.PRODUCTS;
                isError = Boolean.TRUE;
            }
            if (body.get(UserAddressConstants.SnakeCase.ADDRESS_ID) instanceof Long) {
                Long addressId = Long.parseLong(body.get(UserAddressConstants.SnakeCase.ADDRESS_ID).toString());
                if (!userAddressCRUD.isValidAddressId(user, addressId)) {
                    code = null;
                    message = UserAddressConstants.ExceptionMessageCase.INVALID_USER_ADDRESS_ID;
                    missingField = UserAddressConstants.SnakeCase.ADDRESS_ID;
                    isError = true;
                }
            } else if (body.get(LowerCase.PRICE) instanceof Integer) {
                code = null;
                message = MessageCase.THE_VALUE_IS_TOO_HIGH;
                missingField = LowerCase.WEIGHT;
                isError = true;
            } else {
                code = null;
                message = MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                missingField = LowerCase.WEIGHT;
                isError = true;
            }
            if (!isError) {
                isSuccess = Boolean.TRUE;
            }
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
}
