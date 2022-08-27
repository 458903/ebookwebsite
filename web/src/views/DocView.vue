<template>
    <a-layout>
        <a-layout-content
                :style="{ background: '#D8BFD8', padding: '24px', margin: 0, minHeight: '280px' }"
        >
            <h3 v-if="level1.length === 0">对不起，找不到相关文档！</h3>
          <a-row>
              <a-col :span="6">
                  <a-directory-tree
                          v-if="level1.length > 0"
                          :tree-data="level1"
                          @select="onSelect"
                          :defaultSelectedKeys="defaultSelectedKeys"
                          :fieldNames="{ label:'name', value: 'id',key:'id' }"
                          :defaultExpandAll="true"
                  ></a-directory-tree>
              </a-col>
              <a-col :span="18">
                  <div>
                      <h2>{{doc.name}}</h2>
                      <div>
                          <span>阅读数：{{doc.viewCount}}</span> &nbsp; &nbsp;
                          <span>点赞数：{{doc.voteCount}}</span>
                      </div>
                      <a-divider style="height: 2px; background-color: #9999cc"/>
                  </div>
                    <div class="editor-content-view" :innerHTML="html">

                    </div>
                  <div class="vote-div">
                      <a-button type="primary" shape="round" :size="'large'" @click="vote">
                          <template #icon><LikeOutlined /> &nbsp;点赞：{{doc.voteCount}} </template>
                      </a-button>
                  </div>
              </a-col>
          </a-row>
        </a-layout-content>
    </a-layout>
</template>
<script lang="ts">
    import {defineComponent, ref ,onMounted,createVNode} from "vue";
    import {message} from 'ant-design-vue';
    import {Tool} from "@/util/tool";
    import {useRoute} from "vue-router";
    import axios from 'axios';
    import '@wangeditor/editor/dist/css/style.css'
    export default defineComponent({
        name: 'DocView',
        setup() {
            const route = useRoute();
            const docs = ref();
            const html = ref();
            const defaultSelectedKeys = ref();
            defaultSelectedKeys.value = [];
            // 当前选中的文档
            const doc = ref();
            doc.value = {};
            const level1 = ref(); // 一级文档树，children属性就是二级文档
            level1.value = [];
            /**
             * 内容查询
             **/
            const handleQueryContent = (id: number) => {
                axios.get("/doc/find-content/" + id).then((response) => {
                    const data = response.data;
                    if (data.success) {
                        html.value = data.content;
                    } else {
                        message.error(data.message);
                    }
                });
            };
            /**
             * 数据查询
             **/
            const handleQuery = () => {
                axios.get("/doc/all/" + route.query.ebookId).then((response) => {
                    const data = response.data;
                    if (data.success) {
                        docs.value = data.content;

                        level1.value = [];
                        level1.value = Tool.array2Tree(docs.value, 0);

                        if (Tool.isNotEmpty(level1)) {
                            defaultSelectedKeys.value = [level1.value[0].id];
                            handleQueryContent(level1.value[0].id);
                            // 初始显示文档信息
                            doc.value = level1.value[0];
                        }
                    } else {
                        message.error(data.message);
                    }
                });
            };
            const onSelect = (selectedKeys: any, info: any) => {
                console.log('selected', selectedKeys, info);
                if (Tool.isNotEmpty(selectedKeys)) {
                    // 选中某一节点时，加载该节点的文档信息
                    if (info.selectedNodes) {
                        doc.value = info.selectedNodes[0].props;
                    }
                    // 加载内容
                    handleQueryContent(selectedKeys[0]);
                }
            };

            // 点赞
            const vote = () => {
                axios.get('/doc/vote/' + doc.value.id).then((response) => {
                    const data = response.data;
                    if (data.success) {
                        doc.value.voteCount++;
                    } else {
                        message.error(data.message);
                    }
                });
            };
            onMounted(() => {
                handleQuery();
            });

            return {
                level1,
                html,
                onSelect,
                defaultSelectedKeys,
                doc,
                vote
            }
        }
    })
</script>

<style>

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
