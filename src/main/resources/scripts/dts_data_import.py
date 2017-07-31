# encoding:utf-8

"""
@desc 铺路石系统关系数据从Excel中读取导入DB
@author changgen.xu
@date 2016/01/07
"""

import MySQLdb, xlwt, xlrd, json

CONFIG = {
	"source_xls_file":u"C:/Users/user/Desktop/py数据匹配/客户ID匹配20160108_pysource.xls",
    "sheet":"buyer"  # ["buyer","supplier"]
}

# 连接MOLBASE数据库的配置
MOLBASE_DB = {
    "host": "101.251.223.61",
    "port": 3307,
    "database": "molbase",
    "user": "leon_read",
    "password": "molbase2015"
}

# 连接U8中间表数据库的配置
U8_DB = {
    "host": "192.168.13.191",
    "port": 3306,
    "database": "crm",
    "user": "db1_crm",
    "password": "fzgR0GoC"
}

# 连接DTS数据库的配置(测试)
# DTS_DB = {
#     "host": "192.168.13.204",
#     "port": 3306,
#     "database": "dts",
#     "user": "root",
#     "password": "rootroot"
# }

# 连接DTS数据库的配置(生产)
DTS_DB = {
    "host": "192.168.13.190",
    "port": 3306,
    "database": "dts",
    "user": "dts",
    "password": "iceman1113"
}

# util:xls文件数据读取
def xls_import(fname, sheet_name):
	if not fname or "" == fname:return None
	bk = xlrd.open_workbook(fname)
	try:sh = bk.sheet_by_name(sheet_name)
	except:sh = bk.sheet_by_index(0)
	row_list = []
	for i in range(0, sh.nrows):row_list.append(sh.row_values(i))
	bk.unload_sheet(0)
	bk.release_resources()
	return row_list

# util:xls文件数据写出  
def xls_export(data, title, fname):
	if not fname or '' == fname:return
	if not data or not isinstance(data, list):return
	bk = xlwt.Workbook("utf-8")
	sh = bk.add_sheet('Sheet1')
	if title and len(title) > 0:
		for i in range(0, len(title)):sh.row(0).write(i, title[i])
	for j in range(0, len(data)):
		row_data = data[j]
		for k in range(0, len(row_data)):sh.row(j + 1).write(k, row_data[k])
	sh.flush_row_data()
	bk.save(fname)

# 整个导入逻辑处理类
class DTSMapDataImport(object):

	def __init__(self, source, target):
		self.molbase_db = MySQLdb.connect(MOLBASE_DB.get('host'), MOLBASE_DB.get('user'), MOLBASE_DB.get('password'), MOLBASE_DB.get('database'), MOLBASE_DB.get('port'), charset="utf8")
		self.u8_db = MySQLdb.connect(U8_DB.get('host'), U8_DB.get('user'), U8_DB.get('password'), U8_DB.get('database'), U8_DB.get('port'), charset="utf8")
		self.dts_db = MySQLdb.connect(DTS_DB.get('host'), DTS_DB.get('user'), DTS_DB.get('password'), DTS_DB.get('database'), DTS_DB.get('port'), charset="utf8")
		self.source_type = source
		self.target_type = target

	def _close_db(self):
		self.molbase_db.close()
		self.u8_db.close()
		self.dts_db.close()

	# step1.加载dts的meta数据缓存
	def load_dts_meta(self):
		dts_cursor = self.dts_db.cursor(cursorclass=MySQLdb.cursors.DictCursor)
		dts_cursor.execute("select * from object_meta")
		rows = dts_cursor.fetchall()
		self.meta_dict = {}
		for row in rows:self.meta_dict[row["type"]] = row

	# step2.读取xls文件中的关系数据并封装
	def read_xls_data(self):
		self.rows = xls_import(CONFIG["source_xls_file"], CONFIG["sheet"])
		self.source_id_set = set()
		self.target_id_set = set()
		for row in self.rows:
			self.source_id_set.add("'" + str(row[0]) + "'")
			self.target_id_set.add("'" + str(row[1]) + "'")

	# step3.远程db查询id的数据并写入object_data表中
	def query_source_data(self):
		# dts cursor
		dts_cursor = self.dts_db.cursor()

		# source
		molbase_cursor = self.molbase_db.cursor(cursorclass=MySQLdb.cursors.DictCursor)
		source_meta = self.meta_dict[self.source_type]
		molbase_cursor.execute("select %s from %s where %s in(%s)" % (source_meta["index_key"], source_meta["table_name"], source_meta["object_id_key"], ",".join(self.source_id_set)))
		source_result_rows = molbase_cursor.fetchall()
		source_key_arr = source_meta["index_key"].split(",")
		insert_data_sql_list = []
		insert_data_sql = "insert ignore into object_data(`type`,name1,name2,name3,content,object_meta_id) values('%s','%s','%s','%s','%s',%s)"
		
		for row in source_result_rows:
			insert_data_sql_list.append(insert_data_sql % (self.source_type, row[source_key_arr[0]], row[source_key_arr[1]].replace("\'","\\'"), row[source_key_arr[2]].replace("\'","\\'"), json.dumps(row).replace("\'","\\'"), source_meta["id"]))

		# target
		u8_cursor = self.u8_db.cursor(cursorclass=MySQLdb.cursors.DictCursor)
		target_meta = self.meta_dict[self.target_type]
		u8_cursor.execute("select %s from %s where %s in(%s)" % (target_meta["index_key"], target_meta["table_name"], target_meta["object_id_key"], ",".join(self.target_id_set)))
		target_result_rows = u8_cursor.fetchall()
		target_key_arr = target_meta["index_key"].split(",")
		for row in target_result_rows:
			insert_data_sql_list.append(insert_data_sql % (self.target_type, row[target_key_arr[0]], row[target_key_arr[1]].replace("\'","\\'"), row[target_key_arr[2]].replace("\'","\\'"), json.dumps(row).replace("\'","\\'"), target_meta["id"]))

		# insert db
		for sql in insert_data_sql_list:
			print(sql)
			dts_cursor.execute(sql)
		self.dts_db.commit()

		# close db
		molbase_cursor.close()
		u8_cursor.close()
		dts_cursor.close()

	# step4.将关系数据写入object_map
	def write_map_db(self):
		dts_cursor = self.dts_db.cursor()
		for row in self.rows:
			# source data id
			dts_cursor.execute("select id from object_data where `type`=%s and name1=%s", (self.source_type, row[0]))
			data_row = dts_cursor.fetchone()
			if not data_row:continue
			source_data_rowid = data_row[0]
			# target data id
			dts_cursor.execute("select id from object_data where `type`=%s and name1=%s", (self.target_type, row[1]))
			data_row = dts_cursor.fetchone()
			if not data_row:continue
			target_data_rowid = data_row[0]
			dts_cursor.execute("insert ignore into object_map(source_type,source_object_id,source_object_data_id,target_type,target_object_id,target_object_data_id,oper_user) values(%s,%s,%s,%s,%s,%s,%s)", (self.source_type, row[0], source_data_rowid, self.target_type, row[1], target_data_rowid, '管理员导入'))
		self.dts_db.commit()
		dts_cursor.close()
	
	def biz_process_start(self):
		#biz controller
		self.load_dts_meta()
		self.read_xls_data()
		self.query_source_data()
		self.write_map_db()
		#close db
		self._close_db()
# main
if __name__ == '__main__':
	# object的类型映射
	# "customer","u8_supplier"
	# "customer","u8_buyer"
	# "molbase_goods","u8_product"
	biz = DTSMapDataImport("customer", "u8_buyer")
	biz.biz_process_start()
