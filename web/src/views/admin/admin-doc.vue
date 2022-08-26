<template>
    <a-layout>
        <a-layout-content
                :style="{ background: '#D8BFD8', padding: '24px', margin: 0, minHeight: '280px' }"
        >
            <p><a-form
                    layout="inline"
                    :model="param">

                <a-form-item>
                    <a-space size="small">
                    <a-button
                            type="primary"
                            html-type="submit"
                            @click="handleQuery()"
                    ><template #icon><SearchOutlined /></template>
                        查询
                    </a-button>
                        <router-link to="/admin/ebook">
                            <a-button type="text">返回</a-button>
                        </router-link>
                    </a-space>
                </a-form-item>
            </a-form></p><!--查询和返回按钮-->
            <a-table
                    :columns="columns"
                    :row-key="record => record.id"
                    :data-source="level1"
                    :loading="loading"
                    :pagination="false"
            >
                <template #headerCell="{ column }">
                    <template v-if="column.key === 'name'">
                           <span>
                               <smile-outlined />
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
            <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
                <a-form-item label="名称">
                    <a-input v-model:value="doc.name" />
                </a-form-item>
                <a-form-item label="名称">
                    <a-tree-select
                            v-model:value="doc.parent"
                            style="width: 100%"
                            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                            placeholder="请选择父文档"
                            tree-default-expand-all
                            :tree-data="level1"
                            :fieldNames="{children:'children', label:'name', value: 'id',key:'id' }"
                    >
                        <template #suffixIcon><SmileOutlined /></template>
                    </a-tree-select>
                </a-form-item>
                <a-form-item label="顺序">
                    <a-input v-model:value="doc.sort" />
                </a-form-item>
                <a-form-item label="父分类">
                    <a-select
                            ref="select"
                            v-model:value="doc.parent"
                    >
                        <a-select-option value="0">无</a-select-option>
                        <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="doc.id === c.id">
                            {{c.name}}</a-select-option>
                    </a-select>
                </a-form-item>
            </a-form></p>

    </a-modal><!--弹出模态框-->
</template>
<script lang="ts">
    import { defineComponent ,ref,onMounted} from 'vue';
    import axios from 'axios';
    import { message } from 'ant-design-vue';
    import { UserOutlined,SmileOutlined, SearchOutlined} from '@ant-design/icons-vue';
    import {Tool} from "@/util/tool";
    export default defineComponent({
        name:'AdminDoc',
        components: {
            UserOutlined,
            SmileOutlined,
            SearchOutlined,
        },

        /**
         * 页面渲染初始化方法
         */
        setup() {
            const level1=ref();
            const doc = ref({});
            const param = ref();
            param.value = {};
            const docs = ref();
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
                axios.get("/doc/all").then(
                    (response)=>{
                        loading.value=false;
                        const data=response.data;
                        if (data.success){
                            docs.value=data.content;
                            console.log("原始数组：",docs.value);
                            level1.value=[];
                            level1.value=Tool.array2Tree(docs.value,0);
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
                axios.post("/doc/save",doc.value).then(
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
                doc.value=Tool.copy(record);
            };
            /**
             * 新增功能
             */
            const add=()=>{
                modalVisible.value=true;
                doc.value={};
            };
            /**
             * 删除功能
             */
            const del=(id: number)=>{
                axios.delete("/doc/delete/"+id).then((response)=>{
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
                doc,
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
