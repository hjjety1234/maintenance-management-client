function kwl(old, new)
	if old == nil then
		print('old is nil')
		return nil
	end
	if new == nil then
		new = old
	end

	f = io.open(old, 'rb')
	if not f then
		print('Can\'t open ' .. old)
		return nil
	end
	data = f:read('*all')
	f:close()

	data = '\n' .. data
	data = string.gsub(data, '\n(%s*)Log:write', '%1--Log:write')
	data = string.gsub(data, '\n(%s*)WriteLogs', '%1--WriteLogs')
	data = string.sub(data, 2)

	f = io.open(new, 'wb')
	if not f then
		print('Can\'t save ' .. new)
		return nil
	end
	f:write(data)
	f:close()
end

if arg[1] then
	kwl(arg[1])
else
	print('请输入需要处理的文件')
end
