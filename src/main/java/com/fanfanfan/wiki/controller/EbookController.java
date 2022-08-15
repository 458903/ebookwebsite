package com.fanfanfan.wiki.controller;
import com.fanfanfan.wiki.domain.Ebook;
//import com.fanfanfan.wiki.rep.EbookQueryReq;
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
    @GetMapping("/list")
    public List<Ebook> list(){
        return ebookService.list();
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
