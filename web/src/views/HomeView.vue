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
    Contentdsggszg
  </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
//import { UserOutlined, LaptopOutlined, NotificationOutlined } from '@ant-design/icons-vue';
import { defineComponent } from 'vue';
//import HelloWorld from '@/components/HelloWorld.vue'; // @ is an alias to /src
import axios from "axios";
export default defineComponent({
 name: 'HomeView',
  components: {},
  setup(){//功能：通过axios调用电子书列表接口
   //setup方法:vue3新增初始化方法
   console.log("setup");
   axios.get("http://localhost:8097/ebook/list?name=vue")
 .then((response)=>{
   console.log(response);

 })
 }

});
</script>
