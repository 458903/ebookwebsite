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
                <template #cover="{ text: cover }">
                    <img v-if="cover" :src="cover" alt="avatar" />
                </template>

                <template v-slot:action="{ text, record }">
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
                </template><!--编辑和删除按钮-->
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
        <a-form-item label="分类一">
            <a-input v-model:value="ebook.category1Id" />
        </a-form-item>
        <a-form-item label="分类二">
            <a-input v-model:value="ebook.category2Id" />
        </a-form-item>
        <a-form-item label="描述">
            <a-input v-model:value="ebook.description" type="textarea" />
        </a-form-item>
    </a-form></p>

    </a-modal><!--弹出模态框-->
</template>
<script lang="ts">
    import { defineComponent ,ref,onMounted,reactive} from 'vue';
    import axios from 'axios';
    import { message } from 'ant-design-vue';
    import { UserOutlined} from '@ant-design/icons-vue';
    import {Tool} from "@/util/tool";
    interface FormState {
        username: string;
        password: string;
        remember: boolean;
    }
    export default defineComponent({
        name:'AdminEbook',
        components: {
            UserOutlined,
        },
        /**
         * 页面渲染初始化方法
         */
        setup() {
            const ebook = ref({});
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
                    slots: { customRender: 'cover' }
                },
                {
                    title: '名称',
                    dataIndex: 'name'
                },
                {
                    title: '分类',
                    slots: { customRender: 'category' }
                },
                {
                    title: '文档数',
                    dataIndex: 'docCount'
                },
                {
                    title: '阅读数',
                    dataIndex: 'viewCount'
                },
                {
                    title: '点赞数',
                    dataIndex: 'voteCount'
                },
                {
                    title: 'Action',
                    key: 'action',
                    slots: { customRender: 'action' }
                }
            ];
            /**
             * 查询全部列表方法
             */
            const handleQuery=(params: any)=>{
                loading.value=true;
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

            const modalVisible=ref(false);
            const modalLoading=ref(false);
            /**
             * 保存功能：模态框弹出时执行
             */
            const handleModalOk=()=>{
                modalLoading.value=true;
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
            /**
             * 生命周期钩子函数
             */
            onMounted(()=>{
                handleQuery({
                    page:1,
                    size:pagination.value.pageSize});
                });
            return {
                columns,
                ebooks,
                ebook,
                pagination,
                loading,
                handleTableChange,
                edit,
                add,
                del,
                handleModalOk,
                modalLoading,
                modalVisible,
                handleQuery,
                param,
            };
        },
    });
</script>

<style scoped>
    img {
        width: 50px;
        height: 50px;}
</style>
