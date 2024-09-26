<template>
  <div class="upload-file">
		<n-upload
			directory-dnd
			action="/api/parseTableData"
			name="file"
			:data="{ }"
			:on-before-upload="handleBeforeUpload"
			:headers="headers"
			@finish="handleFinish"
			:max="1">
			<n-upload-dragger>
				<div style="margin-bottom: 12px">
					<n-icon size="48" :depth="3">
						<archive-icon />
					</n-icon>
				</div>
				<n-text style="font-size: 16px">
					请上传一个5MB以内的文件
				</n-text>
				<n-p depth="3" style="margin: 8px 0 0 0">
					已支持 md、pdf、docx、txt、csv 等文件格式
				</n-p>
			</n-upload-dragger>
		</n-upload>

  </div>
</template>

<script setup lang="ts">
import {getToken} from "@/store/modules/auth/helper";
const emit = defineEmits(['tableData'])

const token = getToken()

const message = useMessage()

const headers = {
	Authorization: `Bearer ${token}`
}

// 上传之前触发事件
function handleBeforeUpload(){
	// 开启加载条
	console.log('handleBeforeUpload')

	// spinShow.value = true
}


function handleFinish({event,file}: {
	file: UploadFileInfo
	event?: ProgressEvent
}) {
	let responseText = (event?.target as XMLHttpRequest).response;
	let response = JSON.parse(responseText);
	console.log('data',response.data)
	message.success('上传成功！')

	emit('tableData', response.data)

	// showModal.value = false
	// // 关闭加载条
	// spinShow.value = false
}


import {NIcon, NP, NText, NUpload, NUploadDragger, UploadFileInfo, useMessage} from "naive-ui";
</script>

<style scoped >

</style>
