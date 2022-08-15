package com.fanfanfan.wiki.controller;
import com.fanfanfan.wiki.domain.Ebook;
//import com.fanfanfan.wiki.rep.EbookQueryReq;
import com.fanfanfan.wiki.rep.EbookQueryReq;
import com.fanfanfan.wiki.rep.EbookSaveReq;
import com.fanfanfan.wiki.resp.CommonResp;
import com.fanfanfan.wiki.resp.EbookQueryResp;
//import com.fanfanfan.wiki.resp.PageResp;
import com.fanfanfan.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
//import javax.validation.Valid;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;
    /*后端会有很多个接口，为了让前端统一处理逻辑（登录校验、权限校验），需要统一处理后端的返回值*/
    @GetMapping("/list")
    public CommonResp list(EbookQueryReq rep){

        CommonResp<List<EbookQueryResp>> resp = new CommonResp<>();
        List<EbookQueryResp> list=ebookService.list(rep);
        resp.setContent(list);
        return resp;

    }


   /* @GetMapping("/list")
    public CommonResp list(@Valid EbookQueryReq req) {
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }*/

 /*   @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody EbookSaveReq req) {
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }*/

 /*   @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        ebookService.delete(id);
        return resp;
    }*/
}
