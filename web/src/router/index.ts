import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AdminUser from '../views/admin/admin-user.vue'
import AdminEbook from '../views/admin/admin-ebook.vue'
import AdminCategory from '../views/admin/admin-category.vue'
import AdminDoc from '../views/admin/admin-doc.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },{
    path:'/doc',
    name:'DocView',
    component: () => import('../views/DocView.vue')
  },
  {
    path: '/aliyun',
    name: 'AliYun',
    component: ()=>import('../views/AliYun.vue')
  },{
    path:'/admin/user',
    name:'AdminUser',
    component:AdminUser
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('../views/AboutView.vue')
  },
  {
    path: '/admin/ebook',
    name: 'AdminEbook ',
    component: AdminEbook
  }, {
    path: '/admin/category',
    name: 'AdminCategory ',
    component: AdminCategory
  },{
    path: '/admin/doc',
    name: 'AdminDoc ',
    component: AdminDoc
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
