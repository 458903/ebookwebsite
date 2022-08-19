<template>
    <a-layout>
        <a-layout-content
                :style="{ background: '#D8BFD8', padding: '24px', margin: 0, minHeight: '280px' }"
        >
            <p> <a-form
                    layout="inline"
                    :model="param">

                <a-form-item>
                    <a-button
                            type="primary"
                            html-type="submit"
                            @click="handleQuery()"
                    >
                        查询
                    </a-button>
                </a-form-item>
            </a-form></p><!--查询按钮-->
            <a-table
                    :columns="columns"
                    :row-key="record => record.id"
                    :data-source="level1"
                    :loading="loading"
                    :pagination="false"
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
                <template #cover="{ text: cover }">
                    <img v-if="cover" :src="cover" alt="avatar" />
                </template>
            </a-table>
            <div :style="{ padding: '26px 16px 16px' }">
                <a-button ghost @click="add()">新增</a-button>
            </div><!--新增按钮-->
        </a-layout-content>
    </a-layout>
    <a-modal
            v-model:visible="modalVisible"
            title="分类表单"
            :confirm-loading="modalLoading"
            @ok="handleModalOk"
    >
        <p>
            <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="名称">
            <a-input v-model:value="category.name" />
        </a-form-item>
        <a-form-item label="顺序">
            <a-input v-model:value="category.sort" />
        </a-form-item>
        <a-form-item label="父分类">
            <a-input v-model:value="category.parent" />
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
        name:'AdminCategory',
        components: {
            UserOutlined,
            SmileOutlined,
        },
        /**
         * 页面渲染初始化方法
         */
        setup() {
            const level1=ref();
            const category = ref({});
            const param = ref();
            param.value = {};
            const categorys = ref();
            const loading = ref(false);
            const columns = [
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                }, {
                    title: '父分类',
                    dataIndex: 'parent',
                    key: 'parent',
                },{
                    title: '顺序',
                    dataIndex: 'sort',
                    key: 'sort',
                }, {
                    title: 'Action',
                    key: 'action',
                    dataIndex: 'action'
                }
            ];
            /**
             * 查询全部列表方法
             */
            const handleQuery=()=>{
                loading.value=true;
                axios.get("/category/all").then(
                    (response)=>{
                        loading.value=false;
                        const data=response.data;
                        if (data.success){
                            categorys.value=data.content;
                            console.log("原始数组：",categorys.value);
                            level1.value=[];
                            level1.value=Tool.array2Tree(categorys.value,0);
                            console.log("树形结构：",level1);
                        }else{
                            message.error(data.message);
                        }
                    });
            };

            const modalVisible=ref(false);
            const modalLoading=ref(false);
            /**
             * 保存功能：模态框弹出时执行
             */
            const handleModalOk=()=>{
                modalLoading.value=true;
                axios.post("/category/save",category.value).then(
                    (response)=>{
                        modalLoading.value=false;
                       const data=response.data;
                       if(data.success){
                           modalVisible.value=false;
                           handleQuery();
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
                category.value=Tool.copy(record);
            };
            /**
             * 新增功能
             */
            const add=()=>{
                modalVisible.value=true;
                category.value={};
            };
            /**
             * 删除功能
             */
            const del=(id: number)=>{
                axios.delete("/category/delete/"+id).then((response)=>{
                        const data=response.data;
                        if(data.success){
                            handleQuery();
                        }
                    })
            };
            /**
             * 生命周期钩子函数
             */
            onMounted(()=>{
                handleQuery();
            });
            return {
                columns,
              //  categorys,
                category,
                loading,
                level1,
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
