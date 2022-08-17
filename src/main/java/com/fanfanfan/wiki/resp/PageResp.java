package com.fanfanfan.wiki.resp;
import java.util.List;
//返回参数
public class PageResp<T> {
    private long total;
//返回总条数
    private List<T> list;
//返回当前页的列表内容
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PageResp{");
        sb.append("total=").append(total);
        sb.append(", list=").append(list);
        sb.append('}');
        return sb.toString();
    }
}
