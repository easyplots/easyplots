<script setup lang='ts'>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { doRegist,getVerificationCode } from '@/api/user'
import defaultAvatar from '@/assets/avatar.jpg'
import {t} from "@/locales"

interface ModelType {
  password: string
  account: string,
  verificationCode:string
}

const router = useRouter()
// const rPasswordFormItemRef = ref<FormItemInst | null>(null)
const message = useMessage()
const user = ref<ModelType>(Object.create(null))

const handleValidateButtonClick = async (e: MouseEvent) => {
  e.preventDefault()
  const { account, password,verificationCode } = user.value
    if(!validateAccount(account)){
        message.error(t('auth.emailFormatError'))
        user.value.account=''
        return
    }
    if(!verificationCode){
        message.error(t('auth.codeEmpty'))
        return
		}
    user.value.password=''
    await doRegist(account, password,verificationCode)
	//setCookie('blueCat_token', res.blueCat_token)
	message.success(t('auth.signupSuccess'))
	router.push('/Login')
}

function validateAccount(account: string) {
  if (!account)
    return false
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  // return emailRegex.test(account) || phoneRegex.test(account);
    return emailRegex.test(account);
}

const isCounting = ref(false)
const countdown =  ref(60)
const buttonText =  ref(t('auth.getCode'))
async function startCountdown() {

    let checkResult = validateAccount(user.value.account)
		if(!checkResult){
        message.warning('账号格式不正确')
		}

    if (isCounting.value) {
        return;
    }

    await getVerificationCode(user.value.account)

    isCounting.value = true;
    buttonText.value = countdown.value + ' 秒后重试';

    const timer = setInterval(() => {
        countdown.value--;
        buttonText.value = countdown.value + ' 秒后重试';

        if (countdown.value === 0) {
            clearInterval(timer);
            isCounting.value = false;
            countdown.value = 60;
            buttonText.value = '获取验证码';
        }
    }, 1000);
}


</script>

<template>
	<div  id="app">
		<br><br>
		<br><br><br>
		<div class="flex justify-center font-bold" style="font-size: 26px;">
			<h2 >EasyPlots</h2>
		</div>
		<div class="flex justify-center" style="font-size: 22px;margin-top: 10px">
			{{ $t('common.slogan') }}
		</div>
		<br>
		<div
			class="relative w-full mt-10 overflow-hidden bg-white shadow-xl ring-1 ring-gray-900/5 sm:mx-auto sm:h-min sm:max-w-4xl sm:rounded-lg lg:max-w-5xl 2xl:max-w-6xl"
			data-v-0ee1b774=""><!---->
			<div  class="px-6 pt-4 pb-8 sm:px-10" data-v-0ee1b774="">
				<main class="mx-auto sm:max-w-4xl lg:max-w-5xl 2xl:max-w-6xl" data-v-0ee1b774=""><!--[--><!--[--><!--[-->
					<div class="nuxt-loading-indicator"
							 style="position: fixed; top: 0px; right: 0px; left: 0px; pointer-events: none; width: 0%; height: 3px; opacity: 0; background: rgb(45, 212, 191); transition: width 0.1s ease 0s, height 0.4s ease 0s, opacity 0.4s ease 0s; z-index: 999999;"></div>
					<div >
						<div>
							<div  class="flex flex-col justify-center min-h-full my-4 space-y-8 sm:px-6 lg:px-8">
								<div class="sm:mx-auto sm:w-full sm:max-w-md"><h2
									class="text-3xl font-bold tracking-tight text-center text-gray-900">{{ $t('auth.signup') }}</h2>

								</div>
								<div class="sm:mx-auto sm:w-full sm:max-w-sm">
									<form class="space-y-6">
										<div>
											<label for="email" class="block text-sm font-medium text-gray-700">{{ $t('auth.email') }}</label>
											<div class="mt-1">

												<input id="email"  v-model="user.account" :allow-input="(val) => { return !/[^A-Za-z0-9_@.]/g.test(val) }" maxlength="32"  :placeholder="$t('auth.pleaseInput')" name="email"
															 type="email" autocomplete="email" required="true"
															 class="block w-full px-3 py-2 placeholder-gray-400 border border-gray-300 rounded-md shadow-sm appearance-none focus:border-teal-500 focus:outline-none focus:ring-teal-500 sm:text-sm">
											</div>
										</div>
										<div>
											<label  class="block text-sm font-medium text-gray-700">{{ $t('auth.code') }}</label>
											<div class="flex justify-between mt-1">
												<input maxLength="6" v-model="user.verificationCode" :placeholder="$t('auth.pleaseInput')" name="verification_code" type="text" required="true"
															 class="w-2/3 px-3 py-2 placeholder-gray-400 border border-gray-300 rounded-md shadow-sm appearance-none focus:border-teal-500 focus:outline-none focus:ring-teal-500 sm:text-sm">
												<button type="button" @click="startCountdown" :disabled="isCounting" class="w-1/3 px-3 py-2 ml-2 text-sm font-medium text-white bg-teal-500 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-teal-500">{{ buttonText }}</button>
											</div>
										</div>

										<div>
											<label for="password" class="block text-sm font-medium text-gray-700">{{ $t('auth.password') }}</label>
											<div class="mt-1">
												<input id="password" maxLength = "16" v-model="user.password"  :placeholder="$t('auth.pleaseInput')" name="password"
															 type="password"  required="true"
															 class="block w-full px-3 py-2 placeholder-gray-400 border border-gray-300 rounded-md shadow-sm appearance-none focus:border-teal-500 focus:outline-none focus:ring-teal-500 sm:text-sm">
											</div>
											<a href="#/login" style="font-size: 15px;margin-top: 10px;display: block" class="font-semibold text-teal-500 hover:text-teal-600">{{ $t('auth.login') }}</a>
										</div>
										<div>
											<button @click="handleValidateButtonClick" type="submit"
															class="flex inline-flex items-center justify-center w-full px-4 py-2 text-sm font-medium text-white transition bg-teal-500 bg-teal-600 border border-transparent rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:bg-teal-800 hover:bg-teal-700 focus:ring-teal-500 hover:bg-teal-600 focus:ring-teal-400">
												{{ $t('auth.signup') }}</button>
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
				</main>
			</div>
		</div>
	</div>
</template>

<style>
	#app {
		background-image: url('@/assets/background.jpg');
		background-size: cover;
		background-repeat: no-repeat;
	}
</style>
