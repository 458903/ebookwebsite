<template>
  <a-layout>
  <a-layout-sider width="200" style="background: #F08080">
    <a-menu
            v-model:selectedKeys="selectedKeys2"
            v-model:openKeys="openKeys"
            mode="inline"
            theme="dark"
            :style="{ height: '100%', borderRight: 0 }"
    >
      <a-sub-menu key="sub1">
        <template #title>
              <span>
                <user-outlined />
                subnav 1
              </span>
        </template>
        <a-menu-item key="1">option1</a-menu-item>
        <a-menu-item key="2">option2</a-menu-item>
        <a-menu-item key="3">option3</a-menu-item>
        <a-menu-item key="4">option4</a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="sub2">
        <template #title>
              <span>
                <laptop-outlined />
                subnav 2
              </span>
        </template>
        <a-menu-item key="5">option5</a-menu-item>
        <a-menu-item key="6">option6</a-menu-item>
        <a-menu-item key="7">option7</a-menu-item>
        <a-menu-item key="8">option8</a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="sub3">
        <template #title>
              <span>
                <notification-outlined />
                subnav 3
              </span>
        </template>
        <a-menu-item key="9">option9</a-menu-item>
        <a-menu-item key="10">option10</a-menu-item>
        <a-menu-item key="11">option11</a-menu-item>
        <a-menu-item key="12">option12</a-menu-item>
      </a-sub-menu>
    </a-menu>
  </a-layout-sider>
  <a-layout-content
          :style="{ background: '#D8BFD8', padding: '24px', margin: 0, minHeight: '280px' }"
  >
    <!--列表内容-->
    <a-list item-layout="vertical" size="large" :grid="{ gutter: 20, column: 3 }" :data-source="ebook">
      <template #footer>
        <div>
          <b>个人wiki百科</b>
          电子书
        </div>
      </template>
      <template #renderItem="{ item }">
        <a-list-item key="item.name">
          <template #actions>
          <span v-for="{ type, text } in actions" :key="type">
            <component :is="type" style="margin-right: 8px" />
            {{ text }}
          </span>
          </template><!--三个小图标-->
         <!--图片-->
          <a-list-item-meta :description="item.description">
            <template #title>
              <a :href="item.href">{{ item.name}}</a>
            </template>
            <template #avatar><a-avatar :src="item.cover" /></template>
          </a-list-item-meta>
        </a-list-item>
      </template>
    </a-list>

  </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import { StarOutlined, LikeOutlined, MessageOutlined } from '@ant-design/icons-vue';
import { defineComponent ,onMounted,ref} from 'vue';
import axios from "axios";
const listData: Record<string, string>[] = [];
for (let i = 0; i < 23; i++) {
  listData.push({
    href: 'https://www.antdv.com/',
    title: `ant design vue part ${i}`,
    avatar: 'https://joeschmoe.io/api/v1/random',
    description:
            'Ant Design, a design language for background applications, is refined by Ant UED Team.',
    content:
            'We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.',
  });
}
export default defineComponent({
 name: 'HomeView',
  components: {
    StarOutlined,
    LikeOutlined,
    MessageOutlined,
  },
setup(){//功能：通过axios调用电子书列表接口
   //setup方法:vue3新增初始化方法
   console.log("setup");
   const ebook=ref();
   const pagination = {
      onChange: (page: number) => {
        console.log(page);
      }, pageSize: 3,};
  const actions: Record<string, string>[] = [
    { type: 'StarOutlined', text: '156' },
    { type: 'LikeOutlined', text: '156' },
    { type: 'MessageOutlined', text: '2' },
  ];
   onMounted( ()=> {//初始化的逻辑都写到生命周期函数onmOUTED里面，
 // setup就只用放一些参数定义、方法定义，尽量把初始化方法写到生命周期函数里面，免得界面渲染的时候报错
     console.log("onMounted");
    //如果不加上面这行代码，就会报错“ Cannot read properties of null (reading 'parentNode')”
     axios.get(process.env.VUE_APP_SERVER+"/ebook/list")
             .then((response)=>{
               const data=response.data;
               ebook.value=data.content;
               console.log(response);});
   }
   )
    return {
      ebook,
      listData,
      pagination,
      actions,
    }
 }

});
</script>
<style scoped>
  .ant-avatar{
    margin: 5px 0;
    width: 50px;
    height: 50px;
    line-height: 50px;
    border-radius: 8%;
  }
</style>