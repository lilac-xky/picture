import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import UserLogin from '@/views/user/UserLogin.vue'
import UserRegister from '@/views/user/UserRegister.vue'
import UserManage from '@/views/admin/UserManage.vue'
import AddPicture from '@/views/AddPicture.vue'
import PictureManage from '@/views/admin/PictureManage.vue'
import PictureDetail from '@/views/PictureDetail.vue'
import AddPictureBatch from '@/views/AddPictureBatch.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
    },
    {
      path: '/admin/userManage',
      name: 'userManage',
      component: UserManage,
    },
    {
      path: '/user/login',
      name: 'userLogin',
      component: UserLogin,
    },
    {
      path: '/user/register',
      name: 'userRegister',
      component: UserRegister,
    },
    {
      path: '/add_picture',
      name: 'addPicture',
      component: AddPicture,
    },
    {
      path: '/add_picture/batch',
      name: 'batchAddPicture',
      component: AddPictureBatch,
    },
    {
      path: '/picture/:id',
      name: 'picture',
      component: PictureDetail,
      props: true,
    },
    {
      path: '/admin/pictureManage',
      name: 'pictureManage',
      component: PictureManage,
    }
  ],
})

export default router
