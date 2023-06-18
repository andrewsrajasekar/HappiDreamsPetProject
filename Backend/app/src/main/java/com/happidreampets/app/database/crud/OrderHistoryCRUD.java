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

import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.OrderHistoryConstants.ExceptionMessageCase;
import com.happidreampets.app.database.model.OrderHistory;
import com.happidreampets.app.database.model.OrderHistory.ORDERHISTORYCOLUMN;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.model.User;
import com.happidreampets.app.database.repository.OrderHistoryRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;

@Component
public class OrderHistoryCRUD {

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

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

    public JSONObject getOrderHistoryDetails() {
        JSONObject orderHistoryData = new JSONObject();
        Sort sort = null;
        if (getDbFilter() != null && checkAndGetColumnName() != null) {
            sort = Sort.by(getDbFilter().getSortDirection(), checkAndGetColumnName().getColumnName());
        }
        Integer startIndex = getDbFilter() != null ? getDbFilter().getStartIndex() : 0;
        Integer limit = getDbFilter() != null ? getDbFilter().getLimitIndex() : 0;
        Pageable pageable = sort != null ? PageRequest.of(startIndex, limit, sort) : PageRequest.of(startIndex, limit);
        Page<OrderHistory> orderHistoryPage = orderHistoryRepository.findAll(pageable);
        Iterable<OrderHistory> orderHistoryIterable = orderHistoryPage.getContent();
        orderHistoryData.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(orderHistoryIterable).get(ProductConstants.LowerCase.DATA));
        orderHistoryData.put(ProductConstants.LowerCase.INFO, getPageData(orderHistoryPage));
        return orderHistoryData;
    }

    public List<OrderHistory> createOrderHistory(User user, List<Product> products) {
        List<OrderHistory> orderHistories = new ArrayList<>();
        Long addedTime = System.currentTimeMillis();
        for (Product product : products) {
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setUser(user);
            orderHistory.setProduct(product);
            orderHistory.setAddedTime(addedTime);
            orderHistories.add(orderHistory);
        }
        Iterable<OrderHistory> savedOrderHistoryIterable = orderHistoryRepository.saveAll(orderHistories);
        orderHistories.clear();

        savedOrderHistoryIterable.forEach(orderHistories::add);

        return orderHistories;
    }

    public Boolean deleteOrderHistory(Long id) throws Exception {
        OrderHistory orderHistory = orderHistoryRepository.findById(id).orElse(null);
        if (orderHistory == null) {
            throw new Exception(ExceptionMessageCase.ORDER_HISTORY_NOT_FOUND_FOR_GIVEN_ID);
        }
        orderHistoryRepository.delete(orderHistory);
        return true;
    }
}
