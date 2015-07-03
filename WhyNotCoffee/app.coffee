# Cuz fuck java; lazy code so not the prettiest.

class osuFile
    timings = []

    $currentPoints = $('textarea')

    $type = $('.type')
    $amount = $('.amount')
    $timings = $('.timings')
    $snap = $('.snap')
    $offset = $('.offset')
    $exclude = $('.exclude')
    $volStart = $('.volStart')
    $volChange = $('.volChange')

    # snap [2,3,4,6,8,12,16]

    init: () ->
        for snap in [ 2, 3, 4, 6, 8, 12, 16 ]
            $snap.append($('<option />').val(snap).text("1/#{snap}"))

        $snap.find('[value="3"]').prop('selected', 1)

        $timings.on 'change', ->
            $offset.val($timings.val())

        $(document).on 'paste', @updateTimeData

        $(document).on
            'dragover': @stopEvent
            'dragenter': @stopEvent

        $(document).bind 'drop', @updateTimeData

        $('.updateBtn').click =>
            type = +$type.val()
            amount = +$amount.val()

            snap = 1 / +$snap.val()
            bpm = $timings.find(':selected').attr('bpm')

            startOffset = +($offset.val())

            if $exclude.val() == 1 then startOffset = +($offset.val() + 60000 / bpm * snap)

            volStart = +$volStart.val()
            volChange = +$volChange.val()

            _t = $currentPoints.text()

            return if _t.indexOf('[TimingPoints]') == -1
                alert('Missing timing?')

            _t = _t.split('[TimingPoints]')
            console.log(_t)
            #_t.substring(_t.indexOf('[TimingPoints]'))
            _t[1] = @createTimingPoints(type, amount, startOffset, bpm, snap, volStart, volChange).join('\n').trim() + _t[1]
            $currentPoints.text(_t.join('[TimingPoints]\n'))

        @parseTimings()

    stopEvent: (event) ->
        event.preventDefault()
        event.stopPropagation()
        return if !event.dataTransfer
        event.dataTransfer.dropEffect = 'copy';

    updateTimeData: (event) =>
        return if !event.originalEvent.dataTransfer && event.target.tagName in [ 'TEXTAREA', 'INPUT' ]
        event.preventDefault()
        event.stopPropagation();


        return if event.originalEvent.dataTransfer
            items = event.originalEvent.dataTransfer.items;
            blob = items[0].getAsFile();
            reader = new FileReader();
            reader.onload = (event) => # Fuckin async in sync'd shit...
                data = event.target.result

                return if data.indexOf('[TimingPoints]') == -1

                $currentPoints.text(data)
                @parseTimings()

            reader.readAsText(blob)

        data = event.originalEvent.clipboardData.getData("text")

        return if data.indexOf('[TimingPoints]') == -1

        $currentPoints.text(data)

        @parseTimings()

    parseTimings: () ->
        data = $currentPoints.val().match(/\[TimingPoints\]\n([\s\S]*?)(?:\[|\n\n|\n$)/)

        return if !data

        timings = []

        for line in data[1].trim().split('\n')
            line = line.trim().split(',')

            continue if !line[2]

            if +line[1] > 0
                timings.push {time: +line[0], bpm: this.translateBpm(line[1])}

        $timings.empty()

        return if timings.length == 0

        for timing in timings
            $timings.append($('<option />').val(timing.time).attr('bpm',
                timing.bpm).text("#{timing.bpm} BPM at #{this.fixTimeFormat(timing.time)}"))

        $timings.val($timings.find('option').first().val()).trigger('change')

        timings

    createTimingPoints: (isRed, amount, startOffset, bpm, snap, volStart, volChange) ->
        points = []

        for i in [0..--amount]
            point = []

            point.push parseInt(startOffset + ( i * 60000 / bpm * (1 / snap))) # offset
            point.push if isRed then 100 * (600 / bpm) else -100 # BPM
            point.push 4 # "no idea what's the purpose of it"
            point.push 1 #  hitsound set type, 1 = normal, 2 = soft, 3 = drum
            point.push 0 # if it is a custom one, then the number of it, 0 = not a custom hitsound set
            point.push Math.min volStart + volChange * i, 100
            point.push +isRed # inherited
            point.push 0 # kiai time


            points.push point.join ','

        points

    translateBpm: (bpm) -> #translate peppy's weird bpm format to regular number
        bpm = 100 * (600 / bpm) #normalBpm = 100 * 600 / peppyBpm ---> peppyBpm = 100 * 600 / normalBpm

        if (bpm - parseInt(bpm) > 0.99999) #WHY DID PEPPY ROUND THE LAST 2 NUMBERS IN HIS BPM FORMAT, IT RUINS EVERYTHING
            parseInt(bpm) + 1
        else if (bpm - parseInt(bpm) < 0.00001)
            parseInt(bpm)
        else
            Math.round((bpm * 100.0) / 100.0)
    #Won't be displayed correctly if the bpm has more than 3 numbers after decimal point.

    fixTimeFormat: (time) ->
        date = new Date(null)
        date.setMilliseconds(time)
        [ date.getMinutes(), date.getSeconds(), +date.getMilliseconds() ].join ':'


new osuFile().init()
