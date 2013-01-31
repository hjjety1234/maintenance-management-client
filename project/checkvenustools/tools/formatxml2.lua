require "lfs"


--~ luac = '"' .. lfs.currentdir ()..'\\Lua\\luac.exe"'
local myname=arg[0]
local mydir
mydir=string.sub(myname,1,-15)
luac='"'..mydir..'\\Lua\\luac.exe"'


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
function processdirfile(old, new)
	local f, data, luascript



	f = io.open(old, 'rb')
	if not f then
		print('Can\'t open ' .. old)
		return nil
	end
	data = f:read('*all')
	f:close()

	_, _, luascript = string.find(data, '<!%[CDATA%[(.-)%]%]>')

	if luascript then
		f = io.open('temp.lua', 'wb')
		f:write(luascript)
		f:close()


		kwl('temp.lua')

		os.execute(luac .. ' -s -o temp.lua temp.lua')

		f = io.open('temp.lua', 'rb')
		luascript = f:read('*all')
		f:close()
		os.remove('temp.lua')
	end

	data = string.gsub(data, '\r\n', '\n')
	data = string.gsub(data, '\r', '\n')
	data = string.gsub(data, '>%s*\n%s*<', '><')
	data = string.gsub(data, '%s*\n%s*', ' ')
	data = string.gsub(data, '<!%-%-.-%-%->', '')

	if luascript then
		luascript = '<![CDATA[' .. luascript .. ']]>'
		luascript = string.gsub(luascript, "%%", "%%%%")
		data = string.gsub(data, '<!%[CDATA%[.-%]%]>', luascript, 1)
	end

	f = io.open(new, 'wb')
	f:write(data)
	f:close()
end


 if arg[1] then

 	processdirfile(arg[1],arg[1])
 else
	print('path')
 end
