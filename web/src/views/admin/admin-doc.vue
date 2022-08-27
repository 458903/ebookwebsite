<template>
    <a-layout>
        <a-layout-content
                :style="{ background: '#D8BFD8', padding: '24px', margin: 0, minHeight: '280px' }"
        >
            <a-row :gutter="24">
                <a-col :span="12">
                    <p>
                        <a-form
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
                    </a-form>
                    </p><!--查询和返回按钮-->
                    <a-table
                            :v-if="level1.length>0"
                            :columns="columns"
                            :row-key="record => record.id"
                            :data-source="level1"
                            :loading="loading"
                            :pagination="false"
                            size="small"
                            :default-expand-all-rows="true"
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
                                    <a-button type="primary" @click="edit(record)" size="small">
                                        编辑
                                    </a-button>
                                    <a-popconfirm
                                            title="是否确认删除?删除后不可恢复"
                                            ok-text="删除"
                                            cancel-text="否"
                                            @confirm="del(record.id)">
                                        <a-button type="danger" size="small">
                                            删除
                                        </a-button>
                                    </a-popconfirm>
                                </a-space>
                            </template>
                        </template>
                    </a-table>
                </a-col>
                <a-col :span="12">
                    <a-form :model="doc" layout="vertical">
                        <a-form-item>
                        <div style="border: 1px solid #FFE4B5; margin-top: 10px">
                           <Toolbar
                                        :editor="editorRef"
                                        :defaultConfig="toolbarConfig"
                                        :mode="mode"
                                        style="border-bottom: 1px solid #FFE4B5"
                            />
                           <Editor
                                        :defaultConfig="editorConfig"
                                        :mode="mode"
                                        v-model="valueHtml"
                                        style="height: 200px; overflow-y: hidden"
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
                        <a-form-item label="文档名">
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
                        <a-form-item>
                            <a-button type="primary" @click="handlePreviewContent()">
                                <EyeOutlined /> 内容预览
                            </a-button>
                        </a-form-item>
                    </a-form>
                </a-col>
            </a-row>
            <div :style="{ padding: '26px 16px 16px' }">
                <a-form layout="inline" :model="param">
                    <a-form-item>
                        <a-button type="primary" @click="handleModalOk()">
                            保存
                        </a-button>
                    </a-form-item>
                </a-form>
                <a-button ghost @click="add()">新增</a-button>
            </div><!--新增按钮-->
            <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
                <div class="editor-content-view" :innerHTML="previewHtml"></div>
            </a-drawer>
        </a-layout-content>
    </a-layout>


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
            const editor = editorRef.value;
            const valueHtml = ref('<p>hello</p>')
            const toolbarConfig = {}
            const editorConfig = { placeholder: '请输入内容...' }
            /**
             * 组件销毁时，也及时销毁编辑器
             */
            onBeforeUnmount(() => {
                const editor = editorRef.value;
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
            level1.value=[];//新增初始化
            const doc = ref();
             doc.value={};//初始化
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
                axios.get("/doc/all/" + route.query.ebookId).then(
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
             * 保存功能
             */
            const handleModalOk=()=>{
                modalLoading.value=true;
                level1.value=[];
                doc.value.content= editor.getHtml();
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
            };

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
            const handleContent = () => {
                axios.get("/doc/find-content/" + doc.value.id).then((response) => {
                    const data = response.data;
                    if (data.success) {
                        editor.getHtml(data.content);
                    } else {
                        message.error(data.message);
                    }
                });
            };
            /**
             * 编辑功能
             */
            const edit=(record:any)=>{
                editor.getHtml("");
                modalVisible.value=true;
                doc.value=Tool.copy(record);
                handleContent();
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
                editor.getHtml("");
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
            // ----------------富文本预览--------------
            const drawerVisible = ref(false);
            const previewHtml = ref();
            const handlePreviewContent = () => {
                const html = editor.getHtml();
                previewHtml.value = html;
                drawerVisible.value = true;
            };
            const onDrawerClose = () => {
                drawerVisible.value = false;
            };

            /**
             * 生命周期钩子函数
             */
            onMounted(()=>{
                handleQuery();
            });




            return {
                drawerVisible,
                previewHtml,
                onDrawerClose,
                handlePreviewContent,
                editor,
                editorRef,
                valueHtml,
                mode: 'default',
                toolbarConfig,
                editorConfig,
                handleCreated,
                handleChange,
                handleDestroyed,
                handleFocus,
                handleBlur,
                customAlert,
                customPaste,
                handleContent,
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


<style>
    img {
        width: 50px;
        height: 50px;}
    .editor-content-view p,
    .editor-content-view li {
        white-space: pre-wrap; /* 保留空格 */
    }

    .editor-content-view blockquote {
        border-left: 8px solid #d0e5f2;
        padding: 10px 10px;
        margin: 10px 0;
        background-color: #f1f1f1;
    }

    .editor-content-view code {
        font-family: monospace;
        background-color: #eee;
        padding: 3px;
        border-radius: 3px;
    }
    .editor-content-view pre>code {
        display: block;
        padding: 10px;
    }

    .editor-content-view table {
        border-collapse: collapse;
    }
    .editor-content-view td,
    .editor-content-view th {
        border: 1px solid #ccc;
        min-width: 50px;
        height: 20px;
    }
    .editor-content-view th {
        background-color: #f1f1f1;
    }

    .editor-content-view ul,
    .editor-content-view ol {
        padding-left: 20px;
    }

    .editor-content-view input[type="checkbox"] {
        margin-right: 5px;
    }
</style>