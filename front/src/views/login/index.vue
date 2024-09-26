<script setup lang='ts'>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage} from 'naive-ui'
import { LoginFrom } from '@/typings/user'
import defaultAvatar from '@/assets/avatar.jpg'
import { useUserStore } from '@/store/modules/user'
import to from "await-to-js";
import {t} from "@/locales"

const userStore = useUserStore()
const router = useRouter()
const message = useMessage()
const user = ref<LoginFrom>(Object.create(null))

// const rules = {
//   account: {
//     required: true,
//     message: '请输入用户名',
//     trigger: ['input', 'blur'],
//   },
//   password: {
//     required: true,
//     message: '请输入密码',
//     trigger: ['input', 'blur'],
//   },
// }

// 点击登录
async function handleValidateButtonClick(e: MouseEvent){
	e.preventDefault()
	const { username, password } = user.value
	if (!validateAccount(username)) {
		message.error(t('auth.emailFormatError'))
		return
	}
	if (username && password) {
		const [err] = await to(userStore.userLogin(user.value));
		if (!err) {
			message.success(t('auth.loginSuccess'))
			await router.push('/');
		}else{
			message.error(err.message)
		}
	} else {
		message.error(t('auth.emailOrPwdEmpty'))
	}
}

function validateAccount(account:string) {
    if(!account){
        return false
    }
    const phoneRegex = /^1[3456789]\d{9}$/;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(account) || phoneRegex.test(account);;
}


const handleRegistBtnClick = async (e: MouseEvent) => {
  router.push('/regist')
}
</script>

<template>
	<div id="app">
		<br><br>
		<br><br><br>
		<div class="flex justify-center font-bold" style="font-size: 26px;">
			<h2 >EasyPlots</h2>
		</div>
		<div class="flex justify-center" style="font-size: 22px;margin-top: 10px">
			{{ $t('common.slogan') }}
		</div>

		<br>
		<div class="relative w-full mt-10 overflow-hidden bg-white shadow-xl ring-1 ring-gray-900/5 sm:mx-auto sm:h-min sm:max-w-4xl sm:rounded-lg lg:max-w-5xl 2xl:max-w-6xl"
			data-v-0ee1b774="">
			<div class="px-6 pt-4 pb-8 sm:px-10" data-v-0ee1b774="">
				<main class="mx-auto sm:max-w-4xl lg:max-w-5xl 2xl:max-w-6xl" data-v-0ee1b774=""><!--[--><!--[--><!--[-->
					<div class="nuxt-loading-indicator"
						style="position: fixed; top: 0px; right: 0px; left: 0px; pointer-events: none; width: 0%; height: 3px; opacity: 0; background: rgb(45, 212, 191); transition: width 0.1s ease 0s, height 0.4s ease 0s, opacity 0.4s ease 0s; z-index: 999999;">
					</div>
					<div>
						<div>
							<div class="flex flex-col justify-center min-h-full my-4 space-y-8 sm:px-6 lg:px-8">
								<div class="sm:mx-auto sm:w-full sm:max-w-md">
									<h2 class="text-3xl font-bold tracking-tight text-center text-gray-900">{{ $t('auth.login') }}</h2>

								</div>
								<div class="sm:mx-auto sm:w-full sm:max-w-sm">
									<form class="space-y-6">
										<div>
											<label for="email"
												class="block text-sm font-medium text-gray-700">{{ $t('auth.email') }}</label>
											<div class="mt-1">

												<input id="email" v-model="user.username"
													:allow-input="(val: string) => { return !/[^A-Za-z0-9_@.]/g.test(val) }"
													maxlength="32" :placeholder="$t('auth.pleaseInput')" name="email" type="email"
													autocomplete="email" required="true"
													class="block w-full px-3 py-2 placeholder-gray-400 border border-gray-300 rounded-md shadow-sm appearance-none focus:border-teal-500 focus:outline-none focus:ring-teal-500 sm:text-sm">
											</div>
										</div>
										<div>
											<label for="password" class="block text-sm font-medium text-gray-700">{{ $t('auth.password') }}</label>
											<div class="mt-1">
												<input id="password" maxLength="16" v-model="user.password"
													:placeholder="$t('auth.pleaseInput')" name="password" type="password" required="true"
													class="block w-full px-3 py-2 placeholder-gray-400 border border-gray-300 rounded-md shadow-sm appearance-none focus:border-teal-500 focus:outline-none focus:ring-teal-500 sm:text-sm">
											</div>

											<a @click="handleRegistBtnClick" style="font-size: 15px;margin-top: 10px;display: block"
												 class="font-semibold text-teal-500 hover:text-teal-600">{{ $t('auth.signup') }}</a>
											<a href="#/resetpassword"
												style="font-size: 15px; float: right; margin-top: -22PX; margin-bottom: 10px;display: block"
												class="font-semibold text-teal-500 hover:text-teal-600">{{ $t('auth.forgetPassword') }}</a>
										</div>
										<div>

										</div>
										<div>
											<button @click="handleValidateButtonClick"
												class="flex inline-flex items-center justify-center w-full px-4 py-2 text-sm font-medium text-white transition bg-teal-500 bg-teal-600 border border-transparent rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:bg-teal-800 hover:bg-teal-700 focus:ring-teal-500 hover:bg-teal-600 focus:ring-teal-400">
												{{ $t('auth.login') }}</button>
										</div>
									</form>
								</div>
							</div>
						</div>
				</div>
			</main>
		</div>
	</div>

	<div class="footer">

	</div>

</div>

</template>

<style scoped>
#app {
	display: flex;
	flex-direction: column;
	min-height: 100vh;
	/* 确保至少为视口的高度 */
	background-image: url('@/assets/background.jpg');
	background-size: cover;
	background-repeat: no-repeat;
}


.footer {
	display: flex;
	flex-direction: column;
	justify-content: center;
	/* 垂直居中 */
	align-items: center;
	/* 水平居中 */
	text-align: center;
	color: #999999;
	width: 100%;
	margin-top: auto;
	/* 自动将页脚推到底部 */
}

.forgot {
	top: 1px;
	right: 6px;
	font-size: 12px;
	color: var(--font-gray);
}
</style>
