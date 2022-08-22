<template>
    <a-layout>
        <a-layout-content
                :style="{ background: '#D8BFD8', padding: '24px', margin: 0, minHeight: '280px' }"
        >
            <p> <a-form
                    layout="inline"
                    :model="param">
                <a-form-item>
                    <a-input v-model:value="param.name" placeholder="名称">
                        <template #prefix><UserOutlined style="color: rgba(0, 0, 0, 0.25)" /></template>
                    </a-input>
                </a-form-item>
                <a-form-item>
                    <a-button
                            type="primary"
                            html-type="submit"
                            @click="handleQuery({
                    page:1,
                    size:pagination.pageSize})"
                    >
                        查询
                    </a-button>
                </a-form-item>
            </a-form></p><!--查询按钮-->
            <a-table
                    :columns="columns"
                    :row-key="record => record.id"
                    :data-source="ebooks"
                    :pagination="pagination"
                    :loading="loading"
                    @change="handleTableChange"
            >
                <template #headerCell="{ column }">
                    <template v-if="column.key === 'name'">
                           <span><smile-outlined />
                                 名称
                           </span>
                    </template>
                </template>

                <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'name'">
                        <a>
                            {{ record.name }}
                        </a>
                    </template>
                    <template v-else-if="column.key === 'cover'">
                        <img v-if="record.cover" :src="record.cover" alt="avatar" />
                    </template>
                    <template v-else-if="column.key === 'category'">
                       <span>
                         <!--  {{getCategoryName(record.category1Id)}}-->
                           {{ getCategoryName(record.category1Id) }}/{{ getCategoryName(record.category2Id) }}
                       </span>
                    </template>
                    <template v-else-if="column.key === 'action'">
                        <a-space size="small">
                            <a-button type="primary" @click="edit(record)">
                                编辑
                            </a-button>
                            <a-popconfirm
                                    title="是否确认删除?删除后不可恢复"
                                    ok-text="删除"
                                    cancel-text="否"
                                    @confirm="del(record.id)">
                                <a-button type="danger">
                                    删除
                                </a-button>
                            </a-popconfirm>
                        </a-space>
                    </template>
                </template>

            </a-table>
            <div :style="{ padding: '26px 16px 16px' }">
                <a-button ghost @click="add()">新增</a-button>
            </div><!--新增按钮-->
        </a-layout-content>
    </a-layout>
    <a-modal
            v-model:visible="modalVisible"
            title="Title"
            :confirm-loading="modalLoading"
            @ok="handleModalOk"
    >
        <p>
            <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="封面">
            <a-input v-model:value="ebook.cover" />
        </a-form-item>
        <a-form-item label="名称">
            <a-input v-model:value="ebook.name" />
        </a-form-item>
        <a-form-item label="分类">
            <a-cascader
                    v-model:value="categoryIds"
                    expand-trigger="hover"
                    :options="level1"
                    placeholder="Please select"
                    :field-names="{label: 'name',value: 'id',childen:'children'}"
            />
        </a-form-item>
        <a-form-item label="文档数">
            <a-input v-model:value="ebook.docCount" />
        </a-form-item>
        <a-form-item label="描述">
            <a-input v-model:value="ebook.description" type="textarea" />
        </a-form-item>
    </a-form></p>

    </a-modal><!--弹出模态框-->
</template>
<script lang="ts">
    import { defineComponent ,ref,onMounted} from 'vue';
    import axios from 'axios';
    import { message } from 'ant-design-vue';
    import { UserOutlined,SmileOutlined} from '@ant-design/icons-vue';
    import {Tool} from "@/util/tool";
    export default defineComponent({
        name:'AdminEbook',
        components: {
            UserOutlined,
            SmileOutlined,
        },
        /**
         * 页面渲染初始化方法
         */
        setup() {
            const ebook = ref();
            const param = ref();
            param.value = {};
            const ebooks = ref();
            /**
             * 页码常量
             */
            const pagination = ref({
                current: 1,
                pageSize: 5,
                total: 0
            });
            const loading = ref(false);
            const columns = [
                {
                    title: '封面',
                    dataIndex: 'cover',
                    key: 'cover',

                },
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                },
                {
                    title: '分类',
                    dataIndex:  'category',
                    key: 'category',
                },
                {
                    title: '文档数',
                    dataIndex: 'docCount',
                    key: 'docCount',
                },
                {
                    title: '阅读数',
                    dataIndex: 'viewCount',
                    key: 'viewCount',
                },
                {
                    title: '点赞数',
                    dataIndex: 'voteCount',
                    key:'voteCount'
                },
                {
                    title: 'Action',
                    key: 'action',
                    dataIndex: 'action'
                }
            ];
            /**
             * 查询全部列表方法
             */
            const handleQuery=(params: any)=>{
                loading.value=true;
                ebook.value=[];
                axios.get("/ebook/list",{
                    params:{
                        page:params.page,
                        size:params.size,
                        name:param.value.name
                    }
                }).then(
                    (response)=>{
                        loading.value=false;
                        const data=response.data;
                        if (data.success){
                            ebooks.value=data.content.list;
                            //重置分页按钮
                            pagination.value.current=params.page;
                            pagination.value.total=data.content.total;
                        }else{
                            message.error(data.message);
                        }
                    });
            };
            /**
             * 表格点击页码时触发
             */
            const handleTableChange = (pagination: any) => {
                console.log("看看自带的分页参数都有啥：" + pagination);
                handleQuery({
                    page: pagination.current,
                    size: pagination.pageSize
                });
            };

            const categoryIds=ref();

            const modalVisible=ref(false);
            const modalLoading=ref(false);
            /**
             * 保存功能：模态框弹出时执行
             */
            const handleModalOk=()=>{
                modalLoading.value=true;
                ebook.value.category1Id=categoryIds.value[0];
                ebook.value.category2Id=categoryIds.value[1];
                axios.post("/ebook/save",ebook.value).then(
                    (response)=>{
                        modalLoading.value=false;
                       const data=response.data;
                       if(data.success){
                           modalVisible.value=false;
                           handleQuery({
                               page:1,
                               size:pagination.value.pageSize});
                       }else{
                           message.error(data.message);
                       }
                       })
            }
            /**
             * 编辑功能
             */
            const edit=(record:any)=>{
                modalVisible.value=true;
                ebook.value=Tool.copy(record);
                categoryIds.value=[ebook.value.category1Id,ebook.value.category2Id];
            };
            /**
             * 新增功能
             */
            const add=()=>{
                modalVisible.value=true;
                ebook.value={};
            };
            /**
             * 删除功能
             */
            const del=(id: number)=>{
                axios.delete("/ebook/delete/"+id).then((response)=>{
                        const data=response.data;
                        if(data.success){
                            handleQuery({
                                page:1,
                                size:pagination.value.pageSize});
                        }
                    })
            };
            const level1=ref();
            let categorys: any;
            const handlerQueryCategory=()=>{
                loading.value=true;
                axios.get("/category/all").then((response)=>{
                    loading.value=false;
                    const data=response.data;
                    if (data.success){
                        categorys=data.content;
                        console.log("原始数组:",categorys);
                        level1.value=[];
                        level1.value=Tool.array2Tree(categorys,0);
                        console.log("树形结构：",level1.value);
                        handleQuery({
                            page:1,
                            size:pagination.value.pageSize});
                    }else {
                        message.error(data.message);
                    }
                })
            }
            const getCategoryName=(cid: number)=>{
                let result="";
                categorys.forEach((item: any)=>{
                    if (item.id === cid){
                        result=item.name;
                    }
                });
                return  result;
            }
            /**
             * 生命周期钩子函数
             */
            onMounted(()=>{
                handlerQueryCategory();});
            return {
                columns,
                ebooks,
                ebook,
                pagination,
                loading,
                categoryIds,
                level1,
                handleTableChange,
                edit,
                add,
                del,
                handleModalOk,
                modalLoading,
                modalVisible,
                handleQuery,
                param,
                getCategoryName
            };
        },
    });
</script>

<style scoped>
    img {
        width: 50px;
        height: 50px;}
</style>
