<template>
    <div id="app">
        <el-row>
            <el-breadcrumb separator="/" style="height: 40px;">
                <el-breadcrumb-item>校园管理</el-breadcrumb-item>
                <el-breadcrumb-item>课程管理</el-breadcrumb-item>
            </el-breadcrumb>
        </el-row>
        <el-row>
            <el-form :model="queryForm" ref="queryForm" label-width="110px">
                <el-col :span="6">
                    <el-form-item label="名称">
                        <el-input v-model="queryForm.name" clearable></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item label="学年">
                        <el-select v-model="queryForm['schoolYear.id']" filterable clearable placeholder="请输入关键词">
                            <el-option v-for="item in formSelect.options" :key="item.id" :label="item.name"
                                       :value="item.id">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-form>
        </el-row>
        <el-row>
            <el-button @click="searchForm('queryForm')">查询</el-button>
            <el-button @click="toAdd">添加</el-button>
        </el-row>
        <el-row>
            <el-table :data="tableData" border style="width: 100%" stripe>
                <el-table-column prop="name" label="课程名称"/>
                <el-table-column prop="score" label="学分"/>
                <el-table-column prop="hour" label="课时"/>
                <el-table-column prop="schoolYear.name" label="学年"/>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <el-button @click="toShow(scope.$index, scope.row)" type="text" size="small">查看</el-button>
                        <el-button @click="toUpdate(scope.$index, scope.row)" type="text" size="small">编辑</el-button>
                        <el-button @click="toTeacher(scope.$index, scope.row)" type="text" size="small">教师</el-button>
                        <el-button @click.prevent="toDelete(scope.$index, scope.row)" type="text" size="small">删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="queryForm.page"
                    :page-sizes="[10, 20, 50]"
                    :page-size="queryForm.size"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="queryForm.total">
            </el-pagination>
        </el-row>

        <!--表单-->
        <el-dialog :visible.sync="dialog.show">
            <div slot="title" class="header-title">
                <span> {{ dialog.title }}</span>
            </div>
            <el-form :model="dialog.form" label-width="80px" :rules="dialog.rules" ref="dialog.form"
                     style="width: 50%;margin-left: 25%;margin-right: 25%;">
                <input v-model="dialog.form.id" type="hidden"></input>
                <el-form-item label="课程名称" prop="name">
                    <el-input v-model="dialog.form.name" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="学分" prop="score">
                    <el-input-number v-model="dialog.form.score" :min="1" :max="10" label="学分"></el-input-number>
                </el-form-item>
                <el-form-item label="学时" prop="hour">
                    <el-input-number v-model="dialog.form.hour" :min="1" :max="10" label="学时"></el-input-number>
                </el-form-item>
                <el-form-item label="学年" prop="schoolYear">
                    <el-select v-model="dialog.form.schoolYear" filterable clearable placeholder="请输入关键词">
                        <el-option v-for="item in formSelect.options" :key="item.id" :label="item.name"
                                   :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialog.show = false">取 消</el-button>
                <el-button type="primary" @click="validate">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog :visible.sync="teacherDialog.show">
            <div slot="title" class="header-title">
                <span> {{ teacherDialog.title }}</span>
            </div>
            <el-table ref="teacherTable" :data="teacherDialog.tableData" max-height="400px"
                      @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55"/>
                <el-table-column sortable prop="name" label="教师名称"/>
                <el-table-column sortable prop="workNo" label="教师工号"/>
            </el-table>
            <div slot="footer" class="dialog-footer">
                <el-button @click="teacherDialog.show = false">取 消</el-button>
                <el-button type="primary" @click="saveTeacher">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script lang="ts">
    import Vue from 'vue'

    export default Vue.extend({

        data() {
            return {
                queryForm: {
                    name: null,
                    "schoolYear.id": "",
                    page: 1,
                    size: 10,
                    total: 0,
                },
                formSelect: {
                    options: []
                },
                tableData: [],
                dialog: {
                    saveType: 0,
                    title: "添加",
                    show: false,
                    form: {
                        id: "",
                        name: "",
                        score: 5,
                        hour: 20,
                        schoolYear: "",
                    },
                    rules: {
                        name: [{required: true, message: '请输入名称', trigger: 'blur'}],
                        score: [{required: true, message: '请输入学分', trigger: 'blur'}],
                        hour: [{required: true, message: '请输入学时', trigger: 'blur'}],
                        schoolYear: [{required: true, message: '请选择学年', trigger: 'blur'}],
                    }
                },
                teacherDialog: {
                    currentCourse: "",
                    title: "添加",
                    show: false,
                    tableData: [],
                    multipleSelection: []
                }
            }
        },
        created() {
            this.searchForm("queryForm");
            this.loadQueryFormSelect();
        },
        methods: {
            loadQueryFormSelect() {
                let t = this;
                Vue.axios.post("year/all").then(res => {
                    if (res.data.code == 200) {
                        t.formSelect.options = res.data.entity;
                    }
                });
            },
            handleSizeChange(val: number) {
                this.queryForm.size = val;
                this.searchForm('queryForm');
            },
            handleCurrentChange(val: number) {
                this.queryForm.page = val;
                this.searchForm('queryForm');
            },
            //表格多选框 选择触发事件
            handleSelectionChange(val: any) {
                this.teacherDialog.multipleSelection = val;
            },
            searchForm(formName: string) {
                let t = this;
                Vue.axios.post("/course/list", t.$data[formName]).then(function (res) {
                    if (res.data.code == 200) {
                        t.tableData = res.data.entity.content;
                        t.queryForm.page = res.data.entity.pageable.pageNumber+1
                        t.queryForm.size = res.data.entity.pageable.pageSize
                        t.queryForm.total = res.data.entity.totalElements
                    }
                })
            },
            toAdd() {
                this.dialog.show = true;
                this.dialog.title = "添加";
                this.dialog.saveType = 1;
                this.dialog.form = {
                    id: "",
                    name: "",
                    score: 5,
                    hour: 20,
                    schoolYear: "",
                }
            },
            toShow(index: number, row: any) {
                this.dialog.show = true;
                this.dialog.saveType = 0;
                this.dialog.title = "查看";
                this.showDialogForm(row.id)
            },
            toUpdate(index: number, row: any) {
                this.dialog.show = true;
                this.dialog.saveType = 2;
                this.dialog.title = "修改";
                this.showDialogForm(row.id);
            },
            toTeacher: function (index: number, row: any) {
                let t = this;
                t.teacherDialog.currentCourse = row.id;
                t.teacherDialog.show = true;
                t.teacherDialog.title = "选择教师";
                Vue.axios.post("/teacher/all").then(res => {
                    if (res.data.code == 200)
                        t.teacherDialog.tableData = res.data.entity;
                });
                setTimeout(() => {
                    Vue.axios.post("/course/findById/" + row.id).then(function (res) {
                        if (res.data.code == 200) {
                            t.$refs.teacherTable.clearSelection();
                            let teachers = res.data.entity.schoolTeachers;
                            teachers.forEach(value => {
                                let index = t.teacherDialog.tableData.findIndex((value1: never, index: number, obj: never[]) => {
                                    return value1.id === value.id;
                                });
                                t.$refs.teacherTable.toggleRowSelection(t.teacherDialog.tableData[index]);
                            })
                        }
                    })
                }, 100);
            },
            saveTeacher() {
                let t = this;
                let course_id = t.teacherDialog.currentCourse;
                let teachers: Number[] = [];
                if (t.teacherDialog.multipleSelection.length == 0) {
                    t.$message({
                        message: '没有选择老师!',
                        type: 'warning',
                        showClose: true,
                    });
                }
                t.teacherDialog.multipleSelection.forEach(e => {
                    teachers.push(e.id);
                })
                Vue.axios.post("/course/saveCourseTeacher", {
                    id: course_id,
                    teachers: teachers,
                }, {headers: {"content-type": "application/json",}}).then(res => {
                    if (res.data.code == 200) {
                        t.$message({
                            message: '保存成功!',
                            type: 'success',
                            showClose: true,
                        });
                        t.teacherDialog.show = false;
                    }
                });
            },
            toDelete(index: number, row: any) {
                let t = this;
                Vue.axios.post("/course/deleteById/" + row.id).then(function (res) {
                    let data = res.data;
                    if (data.code == 200) {
                        t.$message({
                            message: '保存成功!',
                            type: 'success',
                            showClose: true,
                        });
                        t.searchForm('queryForm')
                    }
                })
            },
            showDialogForm(id: Number) {
                let t = this;
                Vue.axios.post("/course/findById/" + id).then(function (res) {
                    let data = res.data;
                    if (data.code == 200) {
                        t.dialog.form = {
                            id: data.entity.id,
                            name: data.entity.name,
                            score: data.entity.score,
                            hour: data.entity.hour,
                            schoolYear: data.entity.schoolYear.id,
                        }
                    }
                })
            },
            validate() {
                this.$refs['dialog.form'].validate((valid: boolean) => {
                    if (valid) {
                        this.save()
                    } else {
                        return false;
                    }
                });
            },
            save() {
                let t = this;
                let url
                if (this.dialog.saveType === 1) {
                    url = "/course/add";
                } else if (t.dialog.saveType === 2) {
                    url = "/course/update";
                } else {
                    t.dialog.show = false;
                    return;
                }
                Vue.axios.post(url, this.dialog.form).then(res => {
                    if (res.data.code == 200) {
                        t.$message({
                            message: '保存成功!',
                            type: 'success',
                            showClose: true,
                        });
                        t.dialog.show = false;
                        t.searchForm('queryForm')
                    }
                });
            }
        },
    });
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
    .el-form {
        width: 100%;
        margin-left: 0;
        margin-right: 0;
    }

    .el-select {
        width: 100%;
    }

    .el-input-number {
        width: 100%;
    }

</style>
