package com.happidreampets.app.database.utils;

import org.springframework.data.domain.Sort;

public class DbFilter {
    public enum DATAFORMAT{
        JSON("json"),
        POJO("pojo");

        private final String formatName;

        DATAFORMAT(String formatName) {
            this.formatName = formatName;
        }

        public String getFormatName() {
            return formatName;
        }
    }

    private Integer startIndex = 0;
    private Integer limitIndex = 6;
    private Object sortColumn = null;
    private Sort.Direction sortDirection = Sort.Direction.ASC;
    private DATAFORMAT format;

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getLimitIndex() {
        return limitIndex;
    }

    public void setLimitIndex(Integer limitIndex) {
        this.limitIndex = limitIndex;
    }
        
    public Object getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(Object sortColumn) {
        this.sortColumn = sortColumn;
    }

    public DATAFORMAT getFormat() {
        return format;
    }

    public void setFormat(DATAFORMAT format) {
        this.format = format;
    }

    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Sort.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }
}
