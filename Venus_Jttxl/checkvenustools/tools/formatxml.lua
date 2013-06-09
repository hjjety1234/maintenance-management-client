require "lfs"

luac = '"lua\\luac.exe"'

function processfile(old, new, iskwl)
	local f, data, luascript
	print(old)

	f = io.open(old, 'rb')
	data = f:read('*all')
	f:close()

	_, _, luascript = string.find(data, '<!%[CDATA%[(.-)%]%]>')

	if luascript then
		f = io.open('temp.lua', 'wb')
		f:write(luascript)
		f:close()

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
		data = string.gsub(data, '<!%[CDATA%[.-%]%]>', luascript)
	end

	f = io.open(new, 'wb')
	f:write(data)
	f:close()
end


function processdir(dir, iskwl)
	if string.sub(dir,-1) == '\\' then
		dir = string.sub(dir,1,-2)
	end

	local head = {path=dir, next=nil}
	local tail = head
	while head do
		local base = head.path
		for i in lfs.dir(base) do
			if i~='.' and i~='..' and i~='config.xml' then
				local cur = base .. '\\' .. i
				if lfs.attributes(cur, 'mode') == 'directory' then
					tail.next = {path=cur, next=nil}
					tail = tail.next
				else
					if string.sub(cur, -4) == '.xml' or string.sub(cur, -5) == '.wdml' then
						processfile(cur, cur, iskwl)
					end
				end
			end
		end
		head = head.next
	end
end

if arg[1] then
	processdir(arg[1], arg[2])
else
	print('path')
end
