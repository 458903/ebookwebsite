package com.fanfanfan.wiki.service;
import com.fanfanfan.wiki.domain.Ebook;
import com.fanfanfan.wiki.domain.EbookExample;
import com.fanfanfan.wiki.mapper.EbookMapper;
//import com.fanfanfan.wiki.rep.EbookQueryReq;
import com.fanfanfan.wiki.rep.EbookQueryReq;
import com.fanfanfan.wiki.rep.EbookSaveReq;
import com.fanfanfan.wiki.resp.EbookQueryResp;
//import com.fanfanfan.wiki.resp.PageResp;
import com.fanfanfan.wiki.resp.PageResp;
import com.fanfanfan.wiki.util.CopyUtil;
import com.fanfanfan.wiki.util.SnowFlake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {
    private static final Logger LOG=LoggerFactory.getLogger(EbookService.class);
    @Resource
    private EbookMapper ebookMapper;
   @Resource
   private SnowFlake snowFlake;
    public  PageResp<EbookQueryResp>  list(EbookQueryReq rep) {
        //模糊查询的EbookExample
        EbookExample example = new EbookExample();
        EbookExample.Criteria criteria = example.createCriteria();
        if (!ObjectUtils.isEmpty(rep.getName())){
           criteria.andNameLike("%"+rep.getName()+"%");
        }
        PageHelper.startPage(rep.getPage(),rep.getSize());
        //根据EbookExample查出ebookList,ebookList的类型是一个list，里面装的是Ebook对象
        List<Ebook> ebookList = ebookMapper.selectByExample(example);
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        //现在要把List<Ebook>换成List<EbookQueryResp>(为了安全起见，要把响应包装起来)
        List<EbookQueryResp> respList = CopyUtil.copyList(ebookList, EbookQueryResp.class);

        PageResp<EbookQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);

        return pageResp;
    }
    public void save(EbookSaveReq req) {
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            ebook.setId(snowFlake.nextId());
            ebookMapper.insert(ebook);
        } else {
            // 更新
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }
}

   /* @Resource
    private SnowFlake snowFlake;*/

/*
    public PageResp<EbookQueryResp> list(EbookQueryReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        if (!ObjectUtils.isEmpty(req.getCategoryId2())) {
            criteria.andCategory2IdEqualTo(req.getCategoryId2());
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // List<EbookResp> respList = new ArrayList<>();
        // for (Ebook ebook : ebookList) {
        //     // EbookResp ebookResp = new EbookResp();
        //     // BeanUtils.copyProperties(ebook, ebookResp);
        //     // 对象复制
        //     EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);
        //
        //     respList.add(ebookResp);
        // }

        // 列表复制
        List<EbookQueryResp> list = CopyUtil.copyList(ebookList, EbookQueryResp.class);

        PageResp<EbookQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    */
/*

    public void save(EbookSaveReq req) {
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            ebook.setId(snowFlake.nextId());
            ebookMapper.insert(ebook);
        } else {
            // 更新
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }

    public void delete(Long id) {
        ebookMapper.deleteByPrimaryKey(id);
    }
}
*/
