package com.mmall.common;


import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class PageBean<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    //当前页
    private Integer pageNum;

    //每页的数量
    private Integer pageSize;

    //总页数
    private Integer pages;

    //总记录数
    private Long total;

    //是否是第一页
    private Boolean isFirstPage = false;

    //是否是最后一页
    private Boolean isLastPage = false;

    //结果集
    private List<T> list;

    public PageBean(){

    }

    public PageBean(List<T> list){
        if(list instanceof Page){
            Page page = (Page) list;
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();
            this.pages = page.getPages();
            this.list = page;
            this.total = page.getTotal();
        }else if(list instanceof Collection){
            this.pageNum = 1;
            this.pageSize = list.size();
            this.pages = 1;
            this.list = list;
            this.total = (long)list.size();
        }
        if(list instanceof  Collection){
            isFirstPage = pageNum == 1;
            isLastPage = pageNum == pages;
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Boolean getFirstPage() {
        return isFirstPage;
    }

    public void setFirstPage(Boolean firstPage) {
        isFirstPage = firstPage;
    }

    public Boolean getLastPage() {
        return isLastPage;
    }

    public void setLastPage(Boolean lastPage) {
        isLastPage = lastPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
