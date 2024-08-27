<template xmlns:cursor="http://www.w3.org/1999/xhtml">
  <div>
    <div style="display: flex; color: #666">
      <div style="width: 200px; text-align: center;  border-right: 1px solid #ddd; min-height: calc(100vh - 86px)">
        <!-- 第一部分-->
        <div style="padding: 10px 0">
          <div class="category" style="padding: 10px 0" v-for="item in categoryList" :key="item.text" :class="{ 'category-active' : category === item.category}">
            <i v-if="item.icon" :class="item.icon" style="margin-right: 5px"></i>
            <span>{{ item.text }}</span>
          </div>
        </div>

        <!-- 第二部分-->
        <div style="border-top: 1px solid #ddd; text-align: center; padding-top: 10px">
          <div style="padding: 10px 0; display: flex; justify-content: center" class="category" :class="{ 'category-active' : category === 'share'}">
            <div style="width: 75px; text-align: left">
              <i class="el-icon-share" style="margin-right: 5px"></i>
              <span>我的分享</span>
            </div>
          </div>
          <div style="padding: 10px 0; display: flex; justify-content: center" class="category" :class="{ 'category-active' : category === 'trash'}">
            <div style="width: 75px; text-align: left">
              <i class="el-icon-delete" style="margin-right: 5px"></i>
              <span>回收站</span>
            </div>
          </div>
        </div>
      </div>

      <div style="flex: 1">
        <div>
          <div style="padding: 15px; border-bottom: 1px solid #ddd">
            <el-upload
                style="display: inline-block; margin-right: 10px"
                :action="uploadUrl"
                :headers="{ token: user.token }"
                :show-file-list="false"
                :on-success="handleFileSuccess"
            >
              <el-button type="primary">上传文件</el-button>
            </el-upload>
            <el-button type="primary" plain @click="addFolder">新建文件夹</el-button>
            <el-button type="danger" plain @click="delBatch">批量删除</el-button>
          </div>
        </div>

        <div style="padding: 15px">
          <span>全部文件 <i class="el-icon-arrow-right"></i></span>
        </div>

        <div>
          <el-table stripe :data="tableData" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center"></el-table-column>
            <el-table-column label="名称">
              <template v-slot="scope">
                <div v-if="scope.row.unSave" >
                  <i style="color: #409EFF" :class="typeList.find(v => v.text === scope.row.type)?.icon || 'el-icon-file'"></i>
                  <el-input size="mini" style="width: 300px; margin-right: 5px" v-model="scope.row.name"></el-input>
                  <el-button type="primary" size="mini" @click="save(scope.row)">确定</el-button>
                  <el-button type="danger" size="mini" @click="cancel(scope.row)">取消</el-button>
                </div>

                <div style="display: flex;cursor: pointer" v-else @mouseenter="mouseEnter(scope.row)" @mouseleave="mouseLeave(scope.row)">
                  <div style="flex: 1">
                    <i style="color: #409EFF" :class="typeList.find(v => v.text === scope.row.type)?.icon || 'el-icon-file'"></i>
                    <span v-if="!scope.row.unSave" style="margin-left: 5px">{{ scope.row.name }}</span>
                  </div>
                  <div style="color: #409EFF; font-size: 13px" v-if="scope.row.optShow">
                    <el-tooltip content="分享" effect="light" :open-delay="1000">
                      <i class="el-icon-share" style="margin-right: 10px; cursor: pointer"></i>
                    </el-tooltip>
                    <el-tooltip content="下载" effect="light" :open-delay="1000" v-if="scope.row.folder === '否'">
                      <i class="el-icon-download" style="margin-right: 10px; cursor: pointer" @click="download(scope.row.file)"></i>
                    </el-tooltip>
                    <el-tooltip content="删除" effect="light" :open-delay="1000">
                      <i class="el-icon-delete" style="margin-right: 10px; cursor: pointer" @click="del(scope.row.id)"></i>
                    </el-tooltip>
                    <el-tooltip content="重命名" effect="light" :open-delay="1000">
                      <i class="el-icon-rename" style="margin-right: 10px; cursor: pointer" @click="rename(scope.row)"></i>
                    </el-tooltip>
                    <el-tooltip content="复制" effect="light" :open-delay="1000">
                      <i class="el-icon-document-copy" style="cursor: pointer"></i>
                    </el-tooltip>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="updateTime" label="修改时间" width="300"></el-table-column>
            <el-table-column prop="size" label="文件大小（KB）" width="300"></el-table-column>
          </el-table>

        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {

  data() {
    return {
      categoryList: [
        {"text": '全部文件', category: 'all', icon: ''},
        {"text": '图片', category: 'img', icon: 'el-icon-picture-outline'},
        {"text": '视频', category: 'video', icon: 'el-icon-video-play'},
        {"text": '压缩', category: 'zip', icon: 'el-icon-box'},
      ],
      typeList: [
        {text: 'mp3', icon: 'el-icon-mp3'},
        {text: 'mp4', icon: 'el-icon-mp4'},
        {text: 'jpg', icon: 'el-icon-jpg'},
        {text: 'jpeg', icon: 'el-icon-jpeg'},
        {text: 'png', icon: 'el-icon-png'},
        {text: 'pdf', icon: 'el-icon-pdf'},
        {text: 'docx', icon: 'el-icon-docx'},
        {text: 'txt', icon: 'el-icon-text'},
        {text: 'zip', icon: 'el-icon-zip'},
        {text: 'folder', icon: 'el-icon-folder'},
      ],
      category: this.$route.query.category,
      tableData: [],
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      uploadUrl: '',
      folderId: this.$route.query.folderId,
      ids:[]
    }
  },
  mounted() {
    this.load()

    this.uploadUrl = this.$baseUrl + '/diskFiles/add?folder=否'
    if (this.folderId) {  // 如果不这么判断  就会传 undefined到后台 就会报错
      this.uploadUrl += '&folderId=' + this.folderId
    }
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    download(url) {
      window.open(url)  // 文件下载
    },
    mouseEnter(row) {
      if (!row.unSave) {
        this.$set(row, 'optShow', true)
      }
    },
    mouseLeave(row) {
      if (!row.unSave) {
        this.$set(row, 'optShow', false)
      }
    },
    rename(row) {
      this.$set(row, 'unSave', true)
    },
    save(row) {  // 上传文件夹的方法 和编辑后保存
      if (row.id) {  // 编辑
        this.$request.put('/diskFiles/update', row).then(res => {
          if (res.code === '200') {
            this.$message.success("操作成功")
            this.load()
          } else {
            this.$message.error(res.msg)
          }
        })
      } else {
        let url = '/diskFiles/add?folder=是&name=' + row.name
        if (this.folderId) {  // 外层的folderId
          url += '&folderId=' + this.folderId
        }
        this.$request.post(url).then(res => {
          if (res.code === '200') {
            this.$message.success("操作成功")
            this.load()
          } else {
            this.$message.error(res.msg)
          }
        })
      }

    },
    cancel(row) {
      let index = row.$index
      if (row.id) {  // 编辑
        this.load() // 加载最新的数据即可
      } else {  // 新增
        this.tableData.splice(index, 1)
      }

    },
    addFolder() {
      this.tableData.unshift({name: '', type: 'folder', unSave: true})    //  unSave 控制输入框是否显示
    },
      load() {
      this.$request.get('/diskFiles/selectAll').then(res => {
        this.tableData = res.data || []
        this.tableData.forEach(item => {
          this.$set(item, 'optShow', false)   // 强制设置每行  加一个 optShow属性
        })
      })
    },
    handleFileSuccess(res) {
      if (res.code === '200') {
        this.$message.success("上传成功")
        this.load()
      } else {
        this.$message.error('上传失败')
      }
    },
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/diskFiles/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load()
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/diskFiles/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load()
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
  }
}
</script>

<style scoped>
.category {
  cursor: pointer;
}

.category:hover {
  background-color: #f8f8f8;
  color: #000;
}

.category-active {
  color: #409EFF;
}
</style>