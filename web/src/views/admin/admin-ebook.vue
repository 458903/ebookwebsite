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
                        <a-button type="primary" @click="edit">
                            编辑
                        </a-button>
                        <a-button type="danger">
                                删除
                        </a-button>
                    </a-space>
                </template>
            </a-table>
        </a-layout-content>
    </a-layout>
    <a-modal
            v-model:visible="modalVisible"
            title="Title"
            :confirm-loading="modalLoading"
            @ok="handleModalOk"
    ><p> <a-form
            :model="formState"
            name="basic"
            :label-col="{ span: 8 }"
            :wrapper-col="{ span: 16 }"
            autocomplete="off"
            @finish="onFinish"
            @finishFailed="onFinishFailed"
    >
        <a-form-item
                label="Username"
                name="username"
                :rules="[{ required: true, message: 'Please input your username!' }]"
        >
            <a-input v-model:value="formState.username" />
        </a-form-item>

        <a-form-item
                label="Password"
                name="password"
                :rules="[{ required: true, message: 'Please input your password!' }]"
        >
            <a-input-password v-model:value="formState.password" />
        </a-form-item>

        <a-form-item name="remember" :wrapper-col="{ offset: 8, span: 16 }">
            <a-checkbox v-model:checked="formState.remember">Remember me</a-checkbox>
        </a-form-item>

        <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
            <a-button type="primary" html-type="submit">Submit</a-button>
        </a-form-item>
    </a-form></p>

    </a-modal>
</template>
<script lang="ts">
    import { defineComponent ,ref,onMounted,reactive} from 'vue';
    import axios from 'axios';
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
            const param = ref();
            param.value = {};
            const ebooks = ref();
            const pagination = ref({
                current: 1,
                pageSize: 10,
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
            const formState = reactive<FormState>({
                username: '',
                password: '',
                remember: true,
            });
            const onFinish = (values: any) => {
                console.log('Success:', values);
            };

            const onFinishFailed = (errorInfo: any) => {
                console.log('Failed:', errorInfo);
            };
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
                        ebooks.value=data.content.list;
                        //重置分页按钮
                        pagination.value.current=params.page;
                        pagination.value.total=data.content.total;
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
                setTimeout(()=>{
                    modalVisible.value=false;
                    modalLoading.value=false;
                },2000);
            };
            const edit=()=>{
                modalVisible.value=true;
            }

            onMounted(()=>{
                handleQuery({
                    page:1,
                    size:pagination.value.pageSize});
                });
            return {
                columns,
                ebooks,
                pagination,
                loading,
                handleTableChange,
                edit,
                handleModalOk,
                modalLoading,
                modalVisible,
                formState,
                onFinish,
                onFinishFailed,
            };
        },
    });
</script>

<style scoped>
    img {
        width: 50px;
        height: 50px;}
</style>
