<template>
  <div class="json-editor">
    <textarea ref="textarea" v-model="value" />
  </div>
</template>

<script>

import CodeMirror from 'codemirror'
import 'codemirror/lib/codemirror.css'
import 'codemirror/mode/sql/sql.js'
// 替换主题这里需修改名称
import 'codemirror/theme/idea.css'

export default {
  name: 'codeMirror',
  props: {
    value: {
      type: String,
      required: true,
      default: ''
    },
    height: {
      type: String,
      required: true,
      default: '400px'
    }
  },
  data() {
    return {
      editor: null
    }
  },
  computed: {
    newVal () {
      if (this.editor) {
        return this.editor.getValue()
      }
    }
  },
  watch: {
    value(value) {
      this.$emit('input', value)
      const editorValue = this.editor.getValue()
      if (value !== editorValue) {
        this.editor.setValue(this.value)
      }
    },
    height() {
      this.editor.setSize('auto', this.height)
    },
    newVal () {
      if (this.editor) {
        this.$emit('changeTextarea', this.editor.getValue())
      }
    }
  },
  mounted() {
    this.editor = CodeMirror.fromTextArea(this.$refs.textarea, {
      mode: 'text/x-mysql', //语言
      lineNumbers: true, //是否在编辑器左侧显示行号
      lint: true,
      matchBrackets: true, // 括号匹配
      lineWrapping: true,
      tabSize: 2, // 缩进格式
      styleActiveLine: true, // 高亮选中行
      cursorHeight: 0.9,
      // 替换主题这里需修改名称
      theme: 'idea',
      //是否为只读,如果为"nocursor" 不仅仅为只读 连光标都无法在区域聚焦
      readOnly: false
    })
    this.editor.setSize('auto', this.height)
    this.editor.setValue(this.value)
  },
  methods: {
    getValue() {
      return this.editor.getValue()
    },
    setValue(val) {
      if (this.editor) {
        this.editor.setValue(val)
      }
    }
  }
}
</script>

<style scoped>
.json-editor {
  height: 100%;
  font-size: 14px;
  line-height: 20px;
}

.json-editor >>> .CodeMirror {
  /* overflow-y:auto; */
  font-weight: normal
}

.json-editor >>> .CodeMirror-scroll {
}

.json-editor >>> .cm-s-rubyblue span.cm-string {
  color: #F08047;
}
</style>
