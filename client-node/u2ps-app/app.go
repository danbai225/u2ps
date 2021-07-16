package main

import (
	"fyne.io/fyne/app"
	"fyne.io/fyne/container"
	"fyne.io/fyne/widget"
	"os"
	"time"
	"u2ps/client"
	"u2ps/common"
)

type enterEntry struct {
	widget.Entry
}

func newEnterEntry() *enterEntry {
	entry := &enterEntry{}
	entry.ExtendBaseWidget(entry)
	return entry
}

var bt *widget.Button
var state *widget.Label

func main() {
	//converts a  string from UTF-8 to gbk encoding.
	client.IsGui = true
	a := app.New()
	w := a.NewWindow("U2PS")
	entry := newEnterEntry()
	state = widget.NewLabel("Status:No Run")
	errWindow := a.NewWindow("Err")
	f := func() {
		if len(entry.Text) >= 1 {
			if bt.Text == "Run" {
				state.SetText("Status:Run")
				entry.Hidden = true
				bt.Text = "Stop"
				common.Key = entry.Text
				entry.Text = ""
				common.HostInfo = "server.u2ps.com:2251"
				common.MaxRi = 10
				go func() {
					go client.Conn()
					time.Sleep(5 * time.Second)
					if client.SConn == nil {
						state.SetText("Status:No Run")
						bt.Text = "Run"
						entry.Hidden = false
						go client.Exit()
						errWindow.SetContent(container.NewVBox(
							widget.NewLabel("Connection Failure"),
							widget.NewButton("OK", func() {
								errWindow.Hide()
							}),
						))
						errWindow.Show()
					} else if client.Client.Username == "" {
						state.SetText("Status:No Run")
						bt.Text = "Run"
						entry.Hidden = false
						go client.Exit()
						errWindow.SetContent(container.NewVBox(
							widget.NewLabel("Incorrect KEY"),
							widget.NewButton("OK", func() {
								errWindow.Hide()
							}),
						))
						errWindow.Show()
					}
				}()
			}
		} else if bt.Text == "Stop" {
			state.SetText("Status:No Run")
			bt.Text = "Run"
			entry.Hidden = false
			go client.Exit()
		}
	}
	bt = widget.NewButton("Run", f)
	w.SetContent(container.NewVBox(
		widget.NewLabel("Versions:"+common.Versions),
		widget.NewLabel("Please enter the key"),
		entry,
		state,
		bt,
		widget.NewButton("Exit", func() {
			os.Exit(1)
		})))
	w.ShowAndRun()
}
