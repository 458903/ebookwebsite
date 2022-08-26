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
                <a-form-item label="顺序">
                    <a-input v-model:value="doc.sort" />
                </a-form-item>
                <a-form-item label="父分类">
                    <a-tree-select
                            v-model:value="doc.parent"
                            style="width: 100%"
                            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                            placeholder="请选择父文档"
                            tree-default-expand-all
                            :tree-data="treeSelectData"
                            :fieldNames="{ label:'name', value: 'id',key:'id' }"
                    >
                        <template #suffixIcon><SmileOutlined /></template>
                    </a-tree-select>
                </a-form-item>
                <a-form-item label="内容">
                    <div style="border: 1px solid #ccc">
                        <Toolbar
                                style="border-bottom: 1px solid #ccc"
                                :editor="editor"
                                :defaultConfig="toolbarConfig"
                                :mode="mode"
                        />
                        <Editor
                                style="height: 100px; overflow-y: hidden;"
                                v-model="valueHtml"
                                :defaultConfig="editorConfig"
                                :mode="mode"
                                @onCreated="handleCreated"
                                @onChange="handleChange"
                                @onDestroyed="handleDestroyed"
                                @onFocus="handleFocus"
                                @onBlur="handleBlur"
                                @customAlert="customAlert"
                                @customPaste="customPaste"
                        />
                    </div>
                </a-form-item>
            </a-form></p>

    </a-modal><!--弹出模态框-->

</template>
<script lang="ts">
    import { defineComponent ,ref,onMounted,createVNode, onBeforeUnmount, shallowRef } from 'vue';
    import axios from 'axios';
    import { message } from 'ant-design-vue';
    import { UserOutlined,SmileOutlined, SearchOutlined} from '@ant-design/icons-vue';
    import {Tool} from "@/util/tool";
    import {useRoute} from "vue-router";
    import '@wangeditor/editor/dist/css/style.css'
   import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
  //  import ExclamationCircleOutlined from "@ant-design/icons-vue/ExclamationCircleOutlined";
    export default defineComponent({
        name:'AdminDoc',
        components: {
            UserOutlined,
            SmileOutlined,
            SearchOutlined,
            Editor, Toolbar
        },

        /**
         * 页面渲染初始化方法
         */
        setup() {
            // 编辑器实例，必须用 shallowRef
            const editorRef = shallowRef()
            // 内容 HTML
            const valueHtml = ref('<p>hello</p>')
            const toolbarConfig = {}
            const editorConfig = { placeholder: '请输入内容...' }
            /**
             * 组件销毁时，也及时销毁编辑器
             */
            onBeforeUnmount(() => {
                const editor = editorRef.value
                if (editor == null) return
                editor.destroy()
            });
            // 编辑器回调函数
            const handleCreated = (editor: any) => {
                console.log('created', editor);
                editorRef.value = editor; // 记录 editor 实例，重要！
            };
            const handleChange = (editor: { getHtml: () => any; }) => {
                console.log('change:', editor.getHtml());
            };
            const handleDestroyed = (editor: any) => {
                console.log('destroyed', editor);
            };
            const handleFocus = (editor: any) => {
                console.log('focus', editor);
            };
            const handleBlur = (editor: any) => {
                console.log('blur', editor);
            };
            const customAlert = (info: any, type: any) => {
                alert(`【自定义提示】${type} - ${info}`);
            };
            const customPaste = (editor: { insertText: (arg0: string) => void; }, event: any, callback: (arg0: boolean) => void) => {
                console.log('ClipboardEvent 粘贴事件对象', event);
                // 自定义插入内容
                editor.insertText('xxx');
                // 返回值（注意，vue 事件的返回值，不能用 return）
                callback(false); // 返回 false ，阻止默认粘贴行为
                // callback(true) // 返回 true ，继续默认的粘贴行为
            };


            const route=useRoute();
            console.log("路由：", route);
            console.log("route.path：", route.path);
            console.log("route.query：", route.query);
            console.log("route.param：", route.params);
            console.log("route.fullPath：", route.fullPath);
            console.log("route.name：", route.name);
            console.log("route.meta：", route.meta);
            const level1=ref();//level1就是一级文档树
            const doc = ref();
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
            const treeSelectData=ref();
            treeSelectData.value=[];

            /**
             * 查询全部列表方法
             */
            const handleQuery=()=>{
                loading.value=true;
                level1.value=[];
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
                level1.value=[];
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
             * 将某节点及其子孙节点全部置为disabled
             */
            const setDisabled = (treeSelectData: any, id: any) => {
                // console.log(treeSelectData, id);
                // 遍历数组，即遍历某一层节点
                for (let i = 0; i < treeSelectData.length; i++) {
                    const node = treeSelectData[i];
                    if (node.id === id) {
                        // 如果当前节点就是目标节点
                        console.log("disabled", node);
                        // 将目标节点设置为disabled
                        node.disabled = true;

                        // 遍历所有子节点，将所有子节点全部都加上disabled
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            for (let j = 0; j < children.length; j++) {
                                setDisabled(children, children[j].id)
                            }
                        }
                    } else {
                        // 如果当前节点不是目标节点，则到其子节点再找找看。
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            setDisabled(children, id);
                        }
                    }
                }
            };

            const deleteIds: Array<string> = [];
            const deleteNames: Array<string> = [];
            /**
             * 查找整根树枝
             */
            const getDeleteIds = (treeSelectData: any, id: any) => {
                // console.log(treeSelectData, id);
                // 遍历数组，即遍历某一层节点
                for (let i = 0; i < treeSelectData.length; i++) {
                    const node = treeSelectData[i];
                    if (node.id === id) {
                        // 如果当前节点就是目标节点
                        console.log("delete", node);
                        // 将目标ID放入结果集ids
                        // node.disabled = true;
                        deleteIds.push(id);
                        deleteNames.push(node.name);

                        // 遍历所有子节点
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            for (let j = 0; j < children.length; j++) {
                                getDeleteIds(children, children[j].id)
                            }
                        }
                    } else {
                        // 如果当前节点不是目标节点，则到其子节点再找找看。
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            getDeleteIds(children, id);
                        }
                    }
                }
            };
            /**
             * 内容查询
             **/
            const handleQueryContent = () => {
                axios.get("/doc/find-content/" + doc.value.id).then((response) => {
                    const data = response.data;
                    if (data.success) {
                     //   editor.txt.html(data.content)
                    } else {
                        message.error(data.message);
                    }
                });
            };
            /**
             * 编辑功能
             */
            const edit=(record:any)=>{
                modalVisible.value=true;
                doc.value=Tool.copy(record);
                //不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
                treeSelectData.value=Tool.copy(level1.value);
                //为节点添加一个disabled:ture;就会使其变成不可选中
                setDisabled(treeSelectData.value,record.id);
                //为选择树添加一个“无”
                treeSelectData.value.unshift({id:0,name:'无'});
                //unshif:往数组的前面添加一个节点（push是往数组的后面添加一个元素）
            };
            /**
             * 新增功能
             */
            const add=()=>{
                modalVisible.value=true;//显示模态框
                doc.value={
                    ebookId:route.query.ebookId,
                };//模态框中的doc数据清空
                treeSelectData.value=Tool.copy(level1.value);
                //同样往数组前面添加一组‘无’
                treeSelectData.value.unshift({id:'0',name:'无'});
            };
            /**
             * 删除功能
             */
            const del = (id: number) => {
                // 清空数组，否则多次删除时，数组会一直增加
                deleteIds.length = 0;
                deleteNames.length = 0;
                getDeleteIds(level1.value, id);
                /*   Modal.confirm({
                 title: '重要提醒',
                    icon: createVNode(ExclamationCircleOutlined),
                    content: '将删除：【' + deleteNames.join("，") + "】删除后不可恢复，确认删除？",
                    onOk() {
                       */
                        axios.delete("/doc/delete/" + deleteIds.join(",")).then((response) => {
                            const data = response.data; // data = commonResp
                            if (data.success) {
                                // 重新加载列表
                                handleQuery();
                            } else {
                                message.error(data.message);
                            }
                });
            };

            /**
             * 生命周期钩子函数
             */
            onMounted(()=>{
                handleQuery();
                setTimeout(() => {
                    valueHtml.value = '<p>请输入文档内容</p>'
                }, 1500);
            });




            return {
                editorRef,
                valueHtml,
                mode: 'default', // 或 'simple'
                toolbarConfig,
                editorConfig,
                handleCreated,
                handleChange,
                handleDestroyed,
                handleFocus,
                handleBlur,
                customAlert,
                customPaste,
                handleQueryContent,
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
                treeSelectData,
            };
        },
    });
</script>

<style scoped>
    img {
        width: 50px;
        height: 50px;}
</style>
