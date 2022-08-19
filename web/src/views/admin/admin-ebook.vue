<template>
    <a-layout>
        <a-layout-content
                :style="{ background: '#D8BFD8', padding: '24px', margin: 0, minHeight: '280px' }"
        >
            <!--列表内容-->
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
                </template>
            </a-table>
            <div :style="{ padding: '26px 16px 16px' }">
                <a-button ghost @click="add()">新增</a-button>
            </div>
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

    </a-modal>
</template>
<script lang="ts">
    import { defineComponent ,ref,onMounted,reactive} from 'vue';
    import axios from 'axios';
    import { message } from 'ant-design-vue';
    interface FormState {
        username: string;
        password: string;
        remember: boolean;
    }
    export default defineComponent({
        name:'AdminEbook',
        components: {

        },
        setup() {
            const ebook = ref({});
            const param = ref();
            param.value = {};
            const ebooks = ref();
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


            const handleQuery=(params: any)=>{
                loading.value=true;
                axios.get("/ebook/list",{
                    params:{
                        page:params.page,
                        size:params.size
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

            const edit=(record:any)=>{
                modalVisible.value=true;
                ebook.value=record;
            };
            const add=()=>{
                modalVisible.value=true;
                ebook.value={};
            };
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
            };
        },
    });
</script>

<style scoped>
    img {
        width: 50px;
        height: 50px;}
</style>
